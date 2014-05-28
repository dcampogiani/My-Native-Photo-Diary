package com.danielecampogiani.mynativephotodiary.adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielecampogiani.mynativephotodiary.R;
import com.danielecampogiani.mynativephotodiary.persistence.PicturesProvider;

import java.io.File;

public class TimelinePicturesAdapter extends CursorAdapter {

    private final LayoutInflater myInflater;

    public TimelinePicturesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return myInflater.inflate(R.layout.timeline_picture_layout,parent,false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        ImageView imageView = (ImageView)view.findViewById(R.id.timeline_imageView);
        TextView descriptionView = (TextView)view.findViewById(R.id.timeline_picture_description);
        Button deleteButton = (Button)view.findViewById(R.id.delete_button);
        Button shareButton = (Button)view.findViewById(R.id.share_button);

        final int pictureId = cursor.getInt(cursor.getColumnIndex(PicturesProvider.KEY_ID));
        final String description = cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_DESCRIPTION));
        final String uri = cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_URI));


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder.setTitle("Delete");

                alertDialogBuilder
                        .setMessage("Are you sure you want to delete "+ description+ " ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                new DeletePictureAsyncTask(context,pictureId,uri).execute(); // start the background processing


                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(uri)));
                shareIntent.setType("image/jpeg");
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.send_to)));
            }
        });

        descriptionView.setText(cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_DESCRIPTION)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_URI))));
    }
}

class DeletePictureAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    private final Context context;
    private final int id;
    private final String path;

    public DeletePictureAsyncTask(Context context,int id, String path){
        this.context=context;
        this.id=id;
        this.path=path;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {

        ContentResolver cr = context.getContentResolver();
        Uri toDelete = ContentUris.withAppendedId(PicturesProvider.CONTENT_URI, id);
        cr.delete(toDelete,null,null);
        File file = new File(path);
        file.delete();


        return true;
    }
}