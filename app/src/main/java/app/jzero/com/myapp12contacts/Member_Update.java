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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import static app.jzero.com.myapp12contacts.Main.MEMADDR;
import static app.jzero.com.myapp12contacts.Main.MEMEMAIL;
import static app.jzero.com.myapp12contacts.Main.MEMNAME;
import static app.jzero.com.myapp12contacts.Main.MEMPHONE;
import static app.jzero.com.myapp12contacts.Main.MEMPHOTO;
import static app.jzero.com.myapp12contacts.Main.MEMPW;
import static app.jzero.com.myapp12contacts.Main.MEMSEQ;

public class Member_Update extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member__update);
        final Context ctx = Member_Update.this;
        Intent intent = this.getIntent();
        ItemRetrieve query2 = new ItemRetrieve(ctx);
        query2.id = intent.getStringExtra("seq");
        Main.Member m=(Main.Member)new Main.RetrieveService(){
            @Override
            public Object perform() {
                return query2.execute();
            }
        }.perform();
        ImageView profile = findViewById(R.id.profile);
        profile.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(this.getPackageName()+":drawable/"+m.photo, null,null), ctx.getTheme()));
        EditText name = findViewById(R.id.textName);
        name.setText(""+m.name);
        EditText email = findViewById(R.id.changeEmail);
        email.setText(""+m.email);
        EditText phone = findViewById(R.id.changePhone);
        phone.setText(""+m.phone);
        EditText addr = findViewById(R.id.changeAddress);
        addr.setText(""+m.addr);

        findViewById(R.id.confirmBtn).setOnClickListener(
                (View v)->{
                    ItemUpdate query = new ItemUpdate(ctx);
                   // query.name = (name.getText().toString().equals(""))? m.name : name.getText().toString();
                    // 이렇게 널값 방지해도 된다. 지금 나는 setText해서 할 필요는 없다.
                    query.seq = intent.getStringExtra("seq");
                    query.email = email.getText().toString();
                    query.addr = addr.getText().toString();
                    query.phone = phone.getText().toString();
                    query.name = name.getText().toString();
                    new Main.StatusService(){
                        @Override
                        public void perform() {
                            query.execute();
                        }
                    }.perform();
                    Intent intent2 = new Intent(ctx, Member_Detail.class);
                    intent2.putExtra("seq",""+query.seq);
                    startActivity(intent2);
                }
        );
        findViewById(R.id.cancelBtn).setOnClickListener(
                (View v)->{
                    Intent intent2 = new Intent(ctx, Member_Detail.class);
                    intent2.putExtra("seq",""+query2.id);
                    startActivity(intent2);
                }
        );

    }
    private class MemberUpdateQuery extends Main.QueryFactory{
        SQLiteOpenHelper helper; //그냥 헬퍼로 해도 된대. 오픈헬퍼말고
        public MemberUpdateQuery(Context ctx) {
            super(ctx);
            this.helper = new Main.SQLiteHelper(ctx);  //this는 생략 가능.
        }
        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getWritableDatabase();
        }
    }
    private  class ItemUpdate extends  MemberUpdateQuery{
        String  seq, email, phone, addr, name ;
        public ItemUpdate(Context ctx) {
            super(ctx);
        }
        public void execute(){
            this.getDatabase().execSQL(String.format(" UPDATE MEMBER SET EMAIL = '%s' , PHONE = '%s' , ADDR = '%s' , NAME = '%s' WHERE SEQ LIKE '%s' ", email, phone, addr, name,seq ));
        }
    }
    private  class ItemRetrieve extends MemberUpdateQuery {
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
