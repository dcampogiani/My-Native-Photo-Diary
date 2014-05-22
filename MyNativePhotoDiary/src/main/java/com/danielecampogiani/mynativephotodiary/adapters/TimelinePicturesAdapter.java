package com.danielecampogiani.mynativephotodiary.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielecampogiani.mynativephotodiary.R;
import com.danielecampogiani.mynativephotodiary.persistence.PicturesProvider;

/**
 * Created by danielecampogiani on 21/05/14.
 */
public class TimelinePicturesAdapter extends CursorAdapter {

    private LayoutInflater myInflater;

    public TimelinePicturesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return myInflater.inflate(R.layout.timeline_picture_layout,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView)view.findViewById(R.id.timeline_imageView);
        TextView description = (TextView)view.findViewById(R.id.timeline_picture_description);
        Button deleteButton = (Button)view.findViewById(R.id.delete_button);
        Button shareButton = (Button)view.findViewById(R.id.share_button);
        description.setText(cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_DESCRIPTION)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(PicturesProvider.KEY_URI))));
    }
}
