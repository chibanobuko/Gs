//ListViewに１つのセルの情報(message_item.xmlとMessageRecord)を結びつけるためのクラス
package com.example.chiba.gs;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;
/*
class CustomAdapter extends ArrayAdapter<String>{

    static class ViewHolder {
        TextView labelText;
    }

    private LayoutInflater inflater;

    // コンストラクタ
    public CustomAdapter(Context context,int textViewResourceId, ArrayList<String> labelList) {
        super(context,textViewResourceId, labelList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View view = convertView;

        // Viewを再利用している場合は新たにViewを作らない
        if (view == null) {
            inflater =  (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_layout, null);
            TextView label = (TextView)view.findViewById(R.id.tv);
            holder = new ViewHolder();
            holder.labelText = label;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // 特定の行のデータを取得
        String str = getItem(position);

        if (!TextUtils.isEmpty(str)) {
            // テキストビューにラベルをセット
            holder.labelText.setText(str);
        }

        // 行毎に背景色を変える
        if(position%2==0){
            holder.labelText.setBackgroundColor(Color.parseColor("#aa0000"));
        }else{
            holder.labelText.setBackgroundColor(Color.parseColor("#880000"));
        }

        // XMLで定義したアニメーションを読み込む
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.item_motion);
        // リストアイテムのアニメーションを開始
        view.startAnimation(anim);

        return view;
    }
}
*/
//<MessageRecord>はデータクラスMessageRecordのArrayAdapterであることを示している。このアダプターで管理したいデータクラスを記述されば良い。
public class MessageRecordsAdapter extends ArrayAdapter<MessageRecord> {
    private ImageLoader mImageLoader;

    private static class ViewHolder {
        TextView labelText;
        NetworkImageView imageText;
    }

    //アダプターを作成する関数。コンストラクター。クラス名と同じです。
    public MessageRecordsAdapter(Context context) {
        super(context, R.layout.message_item);
        //レイアウトのidmessage_itemのViewを親クラスに設定している
        //キャッシュメモリを確保して画像を取得するクラスを作成。これを使って画像をダウンロードする。Volleyの機能
        mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }
    //表示するViewを返します。これがListVewの１つのセルとして表示されます。表示されるたびに実行されます。
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder holder;


        //convertViewをチェックし、Viewがないときは新しくViewを作成します。convertViewがセットされている時は未使用なのでそのまま再利用します。メモリーに優しい。
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        //レイアウトにある画像と文字のViewを所得します。
        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image1);
        TextView textView = (TextView) convertView.findViewById(R.id.text1);
        holder = new ViewHolder();
        holder.imageText = imageView;
        holder.labelText = textView;
        convertView.setTag(holder);
        //表示するセルの位置からデータをMessageRecordのデータを取得します。
        MessageRecord imageRecord = getItem(position);

            // テキストビューにラベルをセット
            holder.imageText.setImageUrl(imageRecord.getImageUrl(),mImageLoader);
        holder.labelText.setText(imageRecord.getComment());


        //mImageLoaderを使って画像をダウンロードし、Viewにセットします。
        imageView.setImageUrl(imageRecord.getImageUrl(), mImageLoader);
        //Viewに文字をセットします。
        textView.setText(imageRecord.getComment());


 // 行毎に背景色を変える
        if(position%2==0){
            holder.labelText.setBackgroundColor(Color.parseColor("#aa0000"));
        }else{
            holder.labelText.setBackgroundColor(Color.parseColor("#880000"));
        }
Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.item_motion);
        // リストアイテムのアニメーションを開始
        convertView.startAnimation(anim);

        //1つのセルのViewを返します。
        return convertView;
    }
    //データをセットしなおす関数
    public void setMessageRecords(List<MessageRecord> objects) {
        //ArrayAdapterを空にする。
        clear();
        //テータの数だけMessageRecordを追加します。
        for(MessageRecord object : objects) {
            add(object);
        }
        //データの変更を通知します。
        notifyDataSetChanged();
    }
}

