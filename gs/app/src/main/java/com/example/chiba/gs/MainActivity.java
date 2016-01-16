//起動時に実行されるアクティビティーです。１つの画面に１つのアクティビティーが必要です。
//どのアクティビティーが起動時に実行されるのかはAndroidManifestに記述されています。
package com.example.chiba.gs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kii.cloud.storage.KiiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/*
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ListViewのインスタンスを取得
        ListView list = (ListView)findViewById(R.id.listView);

        // リストアイテムのラベルを格納するArrayListをインスタンス化
        ArrayList<String> labelList = new ArrayList<String>();

        // "List Item + ??"を20個リストに追加
        for(int i=1; i<=20; i++){
            labelList.add("List Item "+i);
        }

        // Adapterのインスタンス化
        // 第三引数にlabelListを渡す
        CustomAdapter mAdapter = new CustomAdapter(this, 0, labelList);

        // リストにAdapterをセット
        list.setAdapter(mAdapter);

        // リストアイテムの間の区切り線を非表示にする
        list.setDivider(null);
    }

}
*/

public class MainActivity extends ActionBarActivity {
    //アダプタークラスです。
    private MessageRecordsAdapter mAdapter;
    private final MainActivity self = this;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //起動時にOSから実行される関数です。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		 //Userで追加ここから
        //KiiCloudでのログイン状態を取得します。nullの時はログインしていない。
        KiiUser user = KiiUser.getCurrentUser();
        //自動ログインのため保存されているaccess tokenを読み出す。tokenがあればログインできる
        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");//保存されていない時は""
        //ログインしていない時はログインのactivityに遷移.SharedPreferencesが空の時もチェックしないとLogOutできない。
        if(user == null || token == "") {
            // Intent のインスタンスを取得する。getApplicationContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            // 遷移先の画面を呼び出す
            startActivity(intent);
            //戻れないようにActivityを終了します。
            finish();
        }
        //Userで追加ここまで

            //メイン画面のレイアウトをセットしています。ListView
            setContentView(R.layout.activity_main);

            // リストアイテムのラベルを格納するArrayListをインスタンス化
            //ArrayList<String> labelList = new ArrayList<String>();

            // "List Item + ??"を20個リストに追加
            /*for(int i=1; i<=100; i++){
                labelList.add("List Item "+i);
            }*/

            // Adapterのインスタンス化
            // 第三引数にlabelListを渡す
            //2015/12/26プルダウンSwipeRefreshLayoutの設定
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
            mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
            mSwipeRefreshLayout.setColorScheme(R.color.red, R.color.green, R.color.blue, R.color.yellow);

            // ListViewにデータをセットする
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(self, R.array.array1, android.R.layout.simple_list_item_1);
            ListView listView = (ListView) findViewById(R.id.mylist);
            listView.setAdapter(adapter);


            //アダプターを作成します。newでクラスをインスタンス化しています。
            mAdapter = new MessageRecordsAdapter(this);

            //ListViewのViewを取得
            //2015/12/26

            //ListViewにアダプターをセット。
            listView.setAdapter(mAdapter);

           listView.setDivider(null);
            //一覧のデータを作成して表示します。
            fetch();

    }
        //自分で作った関数です。一覧のデータを作成して表示します。
        private void fetch() {
            //jsonデータをサーバーから取得する通信機能です。Volleyの機能です。通信クラスのインスタンスを作成しているだけです。通信はまだしていません。
            JsonObjectRequest request = new JsonObjectRequest(
                    "http://gashfara.com/test/json.txt" ,//jsonデータが有るサーバーのURLを指定します。
                        null,
                        //サーバー通信した結果、成功した時の処理をするクラスを作成しています。
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                //try catchでエラーを処理します。tryが必要かどうかはtryに記述している関数次第です。
                                try {
                                    //jsonデータを下記で定義したparse関数を使いデータクラスにセットしています。
                                    List<MessageRecord> messageRecords = parse(jsonObject);
                                    //データをアダプターにセットしています。
                                    mAdapter.setMessageRecords(messageRecords);
                                }
                                catch(JSONException e) {
                                    //トーストを表示
                                    Toast.makeText(getApplicationContext(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        //通信結果、エラーの時の処理クラスを作成。
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //トーストを表示
                                Toast.makeText(getApplicationContext(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        //作成した通信クラスをキュー、待ち行列にいれて適当なタイミングで通信します。
                        //VolleyApplicationはnewしていません。これはAndroidManifestで記載しているので起動時に自動的にnewされています。
                        VolleyApplication.getInstance().getRequestQueue().add(request);
        }
                //サーバにあるjsonデータをMessageRecordに変換します。
                private List<MessageRecord> parse(JSONObject json) throws JSONException {
                    //空のMessageRecordデータの配列を作成
                    ArrayList<MessageRecord> records = new ArrayList<MessageRecord>();
                    //jsonデータのmessagesにあるJson配列を取得します。
                    JSONArray jsonMessages = json.getJSONArray("messages");
                    //配列の数だけ繰り返します。
                    for(int i =0; i < jsonMessages.length(); i++) {
                        //１つだけ取り出します。
                        JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                        //jsonの値を取得します。
                        String title = jsonMessage.getString("comment");
                        String url = jsonMessage.getString("imageUrl");

                        //jsonMessageを新しく作ります。
                        MessageRecord record = new MessageRecord(url, title );
                        //MessageRecordの配列に追加します。
                        records.add(record);
                    }

                    return records;
                }
                //2015/12/26pull
                private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // 3秒待機
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 3000);
                    }

                };
                //2015/12/26
                //メニュー関係
                @Override
                public boolean onCreateOptionsMenu(Menu menu) {
                    // Inflate the menu; this adds items to the action bar if it is present.
                    getMenuInflater().inflate(R.menu.menu_main, menu);
                    return true;
                }

                @Override
                public boolean onOptionsItemSelected(MenuItem item) {
                    // Handle action bar item clicks here. The action bar will
                    // automatically handle clicks on the Home/Up button, so long
                    // as you specify a parent activity in AndroidManifest.xml.
                    int id = item.getItemId();

                    //noinspection SimplifiableIfStatement
                    if (id == R.id.action_settings) {
                        return true;
                    }
                //Userで追加ここから
                //ログアウト処理.KiiCloudにはログアウト機能はないのでAccesTokenを削除して対応。
                if (id == R.id.log_out) {
                    //自動ログインのため保存されているaccess tokenを消す。
                    SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    //ログイン画面に遷移
                    // Intent のインスタンスを取得する。getApplicationContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    // 遷移先の画面を呼び出す
                    startActivity(intent);
                    //戻れないようにActivityを終了します。
                    finish();
                    return true;
                }
                    //Userで追加ここまで


                    return super.onOptionsItemSelected(item);
    }

}