package kr.re.ec.zigeon;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.View; // 이벤트리스너의 onItemClick 메소드에서 넘어오는 View 를 사용하기 위해 추가
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;  // 리스트뷰 클래스를 사용하기 위해 추가
import android.widget.AdapterView.OnItemClickListener;  // 리스너 이벤트를 사용하기 위해 추가
import android.widget.AdapterView;  // 이벤트리스너의 onItemClick 메소드에서 넘어오는 AdapterView 를 사용하기 위해 추가
import android.widget.TextView;  // 텍스트뷰 클래스를 사용하기 위해 추가
import android.widget.ArrayAdapter;  // 어댑터클래스를 사용하기 위해 추가


public class CommentListActivity extends Activity implements OnItemClickListener, OnClickListener
{
    // 이벤트 리스너의 재정의 메소드에서도 어댑터를 사용할 수 있도록 멤버변수로 선언한다.
    ArrayAdapter<String> m_adapter;   
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_list);        // 리소스 파일의 레이아웃 구성을 그대로 사용한다.

        // 리스트뷰가 가질 문자열이 동적으로 추가되므로 ArrayList 클래스 객체를 선언하여 사용한다.
        ArrayList<String> list_string = new ArrayList<String>();
        // 어댑터를 할당하여 ArrayList 객체와 항목 출력 리소스를 설정한다.
        m_adapter = new ArrayAdapter<String>(this, R.layout.listview_item_comment, list_string);

        // 리소스 파일에 정의된 id_list 라는 ID의 리스트뷰를 얻어온다.
        ListView list = (ListView) findViewById(R.id.id_list);
        // 리스트뷰에 어댑터를 설정한다.
        list.setAdapter(m_adapter);
        // 리스트뷰에 리스너 인터페이스를 구현한 이 클래스를 넘겨주어 리스너를 설정한다.
        list.setOnItemClickListener(this);

        // 리소스 파일에 정의된 id_btn 이라는 ID의 버튼을 얻어온다.
        Button btn = (Button) findViewById(R.id.id_btn);
        // 버튼에 리스너 인터페이스를 구현한 이 클래스를 넘겨주어 리스너를 설정한다.
        btn.setOnClickListener(this);
    }

    // OnClickListener 인터페이스의 onClick 메소드를 정의한다.
    public void onClick(View view)
    {
        // 어댑터가 가진 데이터(ArrayList)가 몇개의 항목을 가지는지 얻어온다.
        int string_count = m_adapter.getCount();

        // 리소스 파일에 정의된 id_edit 이라는 ID의 에디트텍스트를 얻어온다.
        EditText edit = (EditText) findViewById(R.id.id_edit);
        // 에디트텍스트에 입력된 문자열을 String 타입으로 변경하여 리스트뷰의 맨 끝에 추가한다.
        m_adapter.insert(edit.getText().toString(), string_count);

        // 리소스 파일에 정의된 id_list 이라는 ID의 리스트뷰를 얻어온다.
        ListView list = (ListView) findViewById(R.id.id_list);  
        list.smoothScrollToPosition(string_count);    // 문자열을 추가한 항목쪽으로 스크롤을 이동한다.
        edit.setText("");  // 에디트텍스트에 문자열을 지운다.
    }

    // OnItemClickListener 인터페이스의 onItemClick 메소드를 정의한다.
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // 매개 변수로 넘어온 선택된 항목 View 를 TextView 로 캐스팅한다.
        TextView select_item = (TextView)view;
        // 리소스 파일에 정의된 id_edit 라는 ID의 에디트텍스트를 얻어온다.
        EditText edit = (EditText) findViewById(R.id.id_edit);
        // TextView 로 캐스팅된 선택 항목의 문자열을 얻어서 에디트텍스트에 출력시킨다.
        edit.setText(select_item.getText());
    }
}
