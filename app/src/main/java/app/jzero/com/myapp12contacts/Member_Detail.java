package app.jzero.com.myapp12contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static app.jzero.com.myapp12contacts.Main.MEMADDR;
import static app.jzero.com.myapp12contacts.Main.MEMEMAIL;
import static app.jzero.com.myapp12contacts.Main.MEMNAME;
import static app.jzero.com.myapp12contacts.Main.MEMPHONE;
import static app.jzero.com.myapp12contacts.Main.MEMPHOTO;
import static app.jzero.com.myapp12contacts.Main.MEMPW;
import static app.jzero.com.myapp12contacts.Main.MEMSEQ;

public class Member_Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member__detail);
        final Context ctx = Member_Detail.this;
        Intent intent = this.getIntent();
       // String seq = intent.getExtras().getString("seq");
        ItemRetrieve query = new ItemRetrieve(ctx);
        query.id =intent.getStringExtra("seq");
        Main.Member m=(Main.Member)new Main.RetrieveService(){
            @Override
            public Object perform() {
                return query.execute();
            }
        }.perform();
        ImageView profile = findViewById(R.id.profile);
        Log.d("검색된 이름",""+m.name);
        //int prof = getResources().getIdentifier(this.getPackageName()+":drawable/"+m.photo, null,null);
        profile.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(this.getPackageName()+":drawable/"+m.photo, null,null), ctx.getTheme()));
        TextView name = findViewById(R.id.name);
        name.setText(m.name);
        TextView phone = findViewById(R.id.phone);
        phone.setText(m.phone);
        TextView email = findViewById(R.id.email);
        email.setText(m.email);
        TextView addr = findViewById(R.id.addr);
        addr.setText(m.addr);
        findViewById(R.id.updateBtn).setOnClickListener(
                (View v)->{
                    Intent intent2 = new Intent(ctx, Member_Update.class);
                    intent2.putExtra("seq",""+m.seq);
                    startActivity(intent2);
                }
        );
        findViewById(R.id.callBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.dialBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.smsBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.emailBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.albumBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.movieBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.mapBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.musicBtn).setOnClickListener(
                (View v)->{}
        );
        findViewById(R.id.listBtn).setOnClickListener(
                (View v)->{
                    startActivity(new Intent(ctx,Member_List.class));
                }
        );



    }
    private class MemberDetailQuery extends  Main.QueryFactory{
        SQLiteOpenHelper helper;
        public MemberDetailQuery(Context ctx) {
            super(ctx);
            helper = new Main.SQLiteHelper(ctx);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private  class ItemRetrieve extends  MemberDetailQuery{
        String id;
        public ItemRetrieve(Context ctx) {
            super(ctx);
        }
        public Main.Member execute(){
            Main.Member m = new Main.Member();

            Cursor cursor = this.getDatabase().rawQuery(String.format(" SELECT * FROM MEMBER WHERE "+Main.MEMSEQ+" LIKE %s",id ),null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    m = new Main.Member();
                    m.seq = cursor.getInt(cursor.getColumnIndex(MEMSEQ));
                    m.name = cursor.getString(cursor.getColumnIndex(MEMNAME));
                    m.pw = cursor.getString(cursor.getColumnIndex(MEMPW));
                    m.email = cursor.getString(cursor.getColumnIndex(MEMEMAIL));
                    m.phone = cursor.getString(cursor.getColumnIndex(MEMPHONE));
                    m.photo = cursor.getString(cursor.getColumnIndex(MEMPHOTO));
                    m.addr = cursor.getString(cursor.getColumnIndex(MEMADDR));//"addr"
                }
            }else {
                Log.d("오류 입","니다");
            }
            return m;
        }
    }
}
