package com.takeanumber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PeopleListAdapter extends ArrayAdapter<Person> {

    private Context mContext;
    int mResource;

    public PeopleListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Person> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int ticketNumber = getItem(position).getTicketNum();
        String personName = getItem(position).getName();
        String phoneNumber = getItem(position).getPhoneNum();
        int groupSize = getItem(position).getGroupSize();

        Person person = new Person(ticketNumber, personName, phoneNumber, groupSize);

        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource, parent, false);

        TextView tvTicket = convertView.findViewById(R.id.ticketNumTV);
        TextView tvName = convertView.findViewById(R.id.nameTV);
        TextView tvPhone = convertView.findViewById(R.id.phoneNumTV);
        TextView groupSizeTV = convertView.findViewById(R.id.groupSizeTV);

        tvTicket.setText(String.valueOf(ticketNumber));
        tvName.setText(String.valueOf(personName));
        tvPhone.setText(String.valueOf(phoneNumber));
        groupSizeTV.setText(String.valueOf(groupSize));

        return convertView;

    }
}
