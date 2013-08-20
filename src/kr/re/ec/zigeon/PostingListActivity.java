package kr.re.ec.zigeon;

import android.app.Activity;
import android.os.Bundle;
 
import android.widget.ListView;  // 리스트뷰 클래스를 사용하기 위해 추가
import android.widget.AdapterView.OnItemClickListener;  // 리스너 이벤트를 사용하기 위해 추가
// 이벤트리스너의 onItemClick 메소드에서 넘어오는 AdapterView 를 사용하기 위해 추가
import android.widget.AdapterView;  

import android.widget.ArrayAdapter;  // 어댑터클래스를 사용하기 위해 추가
// 이벤트리스너의 onItemClick 메소드에서 넘어오는 View 를 사용하기 위해 추가
import android.view.View;

import android.content.Intent;

/*
 * 130816 조덕주 작성
 * 
 * 
 */

public class PostingListActivity extends Activity {

    private OnItemClickListener m_item_listener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Intent intent = new Intent(PostingListActivity.this, PostingActivity.class);
            startActivity(intent);
        }
    }; 

    // 리스트뷰에서 사용할 문자열 선언
    static final String[] m_item_string = new String[] {"☆스압주의☆ 오늘 롯데리아 훈남알바 봄!!", "여름 메뉴 추천",
    	"ㅋㅋㅋ 친구들이랑 왔는데..", "와 님들 여기 소프트콘 500원임(냉무)", "아 ㅡㅡ 여기 서비스 개판","내가 아는사람 얘기해줄게.."
    	,"형이 메뉴 추천한다 내껏만 믿어라","아 졸라 시원...","여름 피서는 그냥 롯데리아에서 ...","아 햄버거 드럽게 맛없다!!!"
    	,"님들 버거킹 짱짱임 여기 오지마셈","여긴 24시간이라서 좀 좋네.."};
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_list);
        // listview_item 리소스와 문자열 정보를 저장한 ArrayAdapter 객체를 생성한다.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_item_comment,
                                                                                         m_item_string);

        // 리소스 파일에 정의된 id_list 라는 ID 의 리스트뷰를 얻는다.
        ListView list = (ListView) findViewById(R.id.id_list);
        // 리스트뷰에 ArrayAdapter 객체를 설정하여 리스트뷰에 데이터와 출력 형태를 지정한다.
        list.setAdapter(adapter);
        // 리스너를 설정한다.
        list.setOnItemClickListener(m_item_listener);
    }
}
