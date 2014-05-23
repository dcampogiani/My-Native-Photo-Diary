package com.danielecampogiani.mynativephotodiary.adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
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

/**
 * Created by danielecampogiani on 21/05/14.
 */
public class TimelinePicturesAdapter extends CursorAdapter {

    private LayoutInflater myInflater;
    private static final int ID_TAG = 1;
    private static final int DESCRIPTION_TAG = 2;
    private static final int URI_TAG = 3;

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

        //deleteButton.setTag(ID_TAG,id);
        //deleteButton.setTag(DESCRIPTION_TAG,description);
        //deleteButton.setTag(URI_TAG,uri);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TimelinePicturesAdapter", "User wanna delete "+pictureId+" " + description+" " + uri);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Delete");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete "+ description+ " ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                ContentResolver cr = context.getContentResolver();
                                Uri toDelete = ContentUris.withAppendedId(PicturesProvider.CONTENT_URI, pictureId);
                                cr.delete(toDelete,null,null);
                                File file = new File(uri);
                                file.delete();

                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

        });

        descriptionView.setText(cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_DESCRIPTION)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_URI))));
    }
}