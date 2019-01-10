package com.kbaquri.olaplaystudios;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> implements Filterable {

    public interface SongsAdapterListener {
        void onSongSelected(Song song,int position);

        void onSongPlay(Song song);

        void onSongDownload(Song song);
    }

    private ImageButton currentPlaying;

    private final Context context;
    private final List<Song> songsList;
    private List<Song> songsListFiltered;
    private SongsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView song;
        public TextView artists;
        public CircularImageView coverImage;
        public ImageButton playButton;
        public ImageButton downloadButton;
        public ImageButton starButton;

        public MyViewHolder(View view) {
            super(view);
            song = view.findViewById(R.id.song);
            artists = view.findViewById(R.id.artists);
            coverImage = view.findViewById(R.id.cover_image);
            playButton = view.findViewById(R.id.play);
            downloadButton = view.findViewById(R.id.download);
            starButton = view.findViewById(R.id.star);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSongSelected(songsListFiltered.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }

    public SongsAdapter(Context context, List<Song> songsList, SongsAdapterListener listener) {
        this.context = context;
        this.songsList = songsList;
        this.songsListFiltered = songsList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Song song = songsListFiltered.get(position);
        Glide.with(context).load(song.getCoverImage()).into(holder.coverImage);
        holder.song.setText(song.getSong());
        holder.artists.setText(song.getArtists());
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Play Song
                listener.onSongPlay(songsListFiltered.get(position));

                if (currentPlaying != null) {
                    currentPlaying.setImageResource(R.mipmap.logo);
                }
                currentPlaying = holder.playButton;
                holder.playButton.setImageResource(R.mipmap.logo_pressed);
            }
        });
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Download song
                listener.onSongDownload(songsListFiltered.get(position));
                holder.downloadButton.setImageResource(R.mipmap.down_arrow_pressed);
            }
        });
        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Favourite song
                holder.starButton.setImageResource(R.mipmap.star_button_pressed);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songsListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    songsListFiltered = songsList;
                } else {
                    List<Song> filteredList = new ArrayList<>();
                    for (Song row : songsList) {

                        // here we are looking for song name or artists name match
                        if (row.getSong().toLowerCase().contains(charString) || row.getArtists().toLowerCase().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    songsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = songsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                songsListFiltered = (ArrayList<Song>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
