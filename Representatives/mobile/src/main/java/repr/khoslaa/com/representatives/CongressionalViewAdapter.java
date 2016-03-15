package repr.khoslaa.com.representatives;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

/**
 * Created by akkshay on 3/14/16.
 */

public class CongressionalViewAdapter extends RecyclerView.Adapter<CongressionalViewAdapter.CongressionalViewHolder> {

    private List<RepInfo> repInfoList;
    private Context currContext;

    public CongressionalViewAdapter(List<RepInfo> contactList, Context currContext) {
        this.repInfoList = contactList;
        this.currContext = currContext;
    }


    @Override
    public int getItemCount() {
        return repInfoList.size();
    }

    @Override
    public void onBindViewHolder(CongressionalViewHolder congressionalViewHolder, int i) {
        final RepInfo repInfo = repInfoList.get(i);
        congressionalViewHolder.fullName.setText(repInfo.getFullName() + " (" + repInfo.getParty() + ")");
        congressionalViewHolder.email.setText(repInfo.getEmail());
        congressionalViewHolder.website.setText(repInfo.getWebsite());
        congressionalViewHolder.tweet.setText(repInfo.getTweet());
        congressionalViewHolder.repInfo = repInfo;


        TwitterAuthConfig authConfig = new TwitterAuthConfig("ioPsfwvAp9dJrqyf8jOKH47d9", "HP4CltWNCQGB9cOz9jaoNgXrM58FDE2wUHkVhuuUCly8LfFBb2");
        Fabric.with(currContext, new Twitter(authConfig));

        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                twitterApiClient.getStatusesService().userTimeline(null, repInfo.getTwitterId(), 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        if (listResult != null) {
                            for (Tweet tweet : listResult.data) {
                                Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.createdAt);
                            }
                        }

                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });

        String urlString = "https://theunitedstates.io/images/congress/original/";
        urlString += repInfo.getRepId();
        urlString += ".jpg";
        Picasso.with(currContext)
                .load(urlString)
                .centerCrop()
                .into(congressionalViewHolder.repProfilePic);


    }

    @Override
    public CongressionalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new CongressionalViewHolder(itemView);
    }

    public static class CongressionalViewHolder extends RecyclerView.ViewHolder {

        protected TextView fullName;
        protected TextView email;
        protected TextView website;
        protected TextView tweet;
        protected Button moreButton;
        protected RepInfo repInfo;
        protected TweetView tweetView;
        protected CircleImageView repProfilePic;
        public CongressionalViewHolder(View v) {
            super(v);
            fullName =  (TextView) v.findViewById(R.id.repName);
            email = (TextView)  v.findViewById(R.id.repEmail);
            website = (TextView) v.findViewById(R.id.repWebsite);
            moreButton = (Button) v.findViewById(R.id.moreButton);
            tweetView = (TweetView) v.findViewById(R.id.tweetView);
            repProfilePic = (CircleImageView) v.findViewById(R.id.repProfilePic);

            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailedViewActivity.class);
                    intent.putExtra("FULL_NAME", repInfo.getFullName());
                    intent.putExtra("EMAIL", repInfo.getEmail());
                    intent.putExtra("WEBSITE", repInfo.getWebsite());
                    intent.putExtra("TWEET", repInfo.getTweet());
                    intent.putExtra("PARTY", repInfo.getParty());
                    intent.putExtra("TERM_ENDS", repInfo.getTermEnds());
                    intent.putExtra("REP_ID", repInfo.getRepId());
                    view.getContext().startActivity(intent);

                }
            });


        }

    }


}

