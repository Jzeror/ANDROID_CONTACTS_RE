package app.jzero.com.myapp12contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static app.jzero.com.myapp12contacts.Main.MEMADDR;
import static app.jzero.com.myapp12contacts.Main.MEMEMAIL;
import static app.jzero.com.myapp12contacts.Main.MEMNAME;
import static app.jzero.com.myapp12contacts.Main.MEMPHONE;
import static app.jzero.com.myapp12contacts.Main.MEMPHOTO;
import static app.jzero.com.myapp12contacts.Main.MEMPW;
import static app.jzero.com.myapp12contacts.Main.MEMSEQ;
import static app.jzero.com.myapp12contacts.Main.Member;

public class Member_List extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member__list);
        final Context ctx = Member_List.this;
        ItemList query = new ItemList(ctx);
        /*ArrayList<Main.Member> list = (ArrayList<Main.Member>)new Main.ListService(){

            @Override
            public List<?> perform() {
                return query.execute();
            }
        }.perform();*/ //식을 바로 던져 버린다.
        ListView memberList = findViewById(R.id.memberList);
        memberList.setAdapter(new MemberAdapter(ctx, (ArrayList<Main.Member>)new Main.ListService(){

            @Override
            public List<?> perform() {
                return query.execute();
            }
        }.perform()));
        memberList.setOnItemClickListener(
                (AdapterView<?> p, View v, int i, long l)->{
                    Intent intent =new Intent(ctx, Member_Detail.class);
                    Main.Member m = (Main.Member) memberList.getItemAtPosition(i);
                    intent.putExtra("seq", m.seq+"");
                    startActivity(intent);
                }
        );
        memberList.setOnItemLongClickListener(
                (AdapterView<?> p, View v, int i, long l)->{
                    Main.Member m=(Main.Member)memberList.getItemAtPosition(i);
                    new AlertDialog.Builder(ctx).setTitle("DELETE")
                            .setMessage("삭제하시겠습니까?")
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    ItemDelete query = new ItemDelete(ctx);
                                    query.id = m.seq+"";
                                    new Main.StatusService(){
                                        @Override
                                        public void perform() {
                                            query.execute();
                                        }
                                    }.perform();
                                    Toast.makeText(ctx, "삭제되었습니다", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ctx, Member_List.class));
                                }
                            })
                            .setNegativeButton(android.R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            Toast.makeText(ctx, "취소하였습니다", Toast.LENGTH_LONG).show();
                                        }
                                    }).show();
                    return true;
                }
        );

    }
    private class MemberListQuery extends Main.QueryFactory {
        SQLiteOpenHelper helper;

        public MemberListQuery(Context ctx) {
            super(ctx);
            helper = new Main.SQLiteHelper(ctx);
        }

        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class MemberDeleteQuery extends  Main.QueryFactory{
        SQLiteOpenHelper helper;

        public MemberDeleteQuery(Context ctx) {
            super(ctx);
            helper=new Main.SQLiteHelper(ctx);
        }
        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getWritableDatabase();
        }
    }
    private class ItemList extends MemberListQuery {
        public ItemList(Context ctx) {
            super(ctx);
        }
        public ArrayList<Main.Member> execute() {
            ArrayList<Main.Member> list = new ArrayList<>();
            Cursor cursor = this.getDatabase().rawQuery(" SELECT * FROM MEMBER ", null);
            Main.Member member = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    member = new Main.Member();
                    member.seq = cursor.getInt(cursor.getColumnIndex(MEMSEQ));
                    member.name = cursor.getString(cursor.getColumnIndex(MEMNAME));
                    member.pw = cursor.getString(cursor.getColumnIndex(MEMPW));
                    member.email = cursor.getString(cursor.getColumnIndex(MEMEMAIL));
                    member.phone = cursor.getString(cursor.getColumnIndex(MEMPHONE));
                    member.photo = cursor.getString(cursor.getColumnIndex(MEMPHOTO));
                    member.addr = cursor.getString(cursor.getColumnIndex(MEMADDR));//"addr"
                    list.add(member);
                }
                Log.d("등록된 회원 수가", list.size() + "");
            } else {
                Log.d("등록된 회원이", "없습니다");
            }
            return list;
        }
    }
    private class ItemDelete extends  MemberDeleteQuery{
        String id;
        public ItemDelete(Context ctx) {
            super(ctx);
        }
        public void execute(){
            this.getDatabase().execSQL(String.format(" DELETE FROM MEMBER WHERE SEQ LIKE '%s' ", id));
        }
    }


    private class MemberAdapter extends BaseAdapter{
        ArrayList<Main.Member> list;
        LayoutInflater inflater ;
        Context this__;
        public MemberAdapter(Context ctx, ArrayList<Member> list) {
            this.list = list;
            this.inflater = LayoutInflater.from(ctx);
            this.this__ = ctx;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View v, ViewGroup g) {
            ViewHolder holder;
            if(v==null){
                v = inflater.inflate(R.layout.member_item, null);
                holder = new ViewHolder();
                holder.profile = v.findViewById(R.id.profile);
                holder.name = v.findViewById(R.id.name);
                holder.phone = v.findViewById(R.id.phone);
                v.setTag(holder);
            }else{
                holder = (ViewHolder) v.getTag();
            }

            ItemProfile query = new ItemProfile(this__);
            query.seq = list.get(i).seq+"";

            holder.profile.setImageDrawable(
                    getResources().getDrawable(
                            getResources().getIdentifier(
                                    this__.getPackageName()+":drawable/"
                                            + (new Main.RetrieveService() {
                                        @Override
                                        public Object perform() {
                                            return query.execute();
                                        }
                                    }.perform())
                                    , null, null
                            ), this__.getTheme()
                    )
            );

            holder.name.setText(list.get(i).name);
            holder.phone.setText(list.get(i).phone);
            return v;
        }
    }
    static class ViewHolder{
        ImageView profile;
        TextView name, phone;
    }
    private class MemberProfileQuery extends Main.QueryFactory {
        SQLiteOpenHelper helper;
        public MemberProfileQuery(Context ctx) {
            super(ctx);
            helper = new Main.SQLiteHelper(ctx);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class ItemProfile extends MemberProfileQuery{
        String seq;
        public ItemProfile(Context ctx) {
            super(ctx);
        }
        public String execute(){
            Cursor c = getDatabase()
                    .rawQuery(String.format(
                            " SELECT %s FROM %s WHERE %s LIKE '%s' "
                            , MEMPHOTO, Main.MEMTAB, MEMSEQ, seq),null);
            String result = "";
            if(c != null){
                if(c.moveToNext()){
                    result = c.getString(c.getColumnIndex(MEMPHOTO));
                }
            }
            return result;
        }
    }
}