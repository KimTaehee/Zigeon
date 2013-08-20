package kr.re.ec.zigeon;

import android.app.Activity;
import android.os.Bundle;
 
import android.widget.ListView;  // ����Ʈ�� Ŭ������ ����ϱ� ���� �߰�
import android.widget.AdapterView.OnItemClickListener;  // ������ �̺�Ʈ�� ����ϱ� ���� �߰�
// �̺�Ʈ�������� onItemClick �޼ҵ忡�� �Ѿ���� AdapterView �� ����ϱ� ���� �߰�
import android.widget.AdapterView;  

import android.widget.ArrayAdapter;  // �����Ŭ������ ����ϱ� ���� �߰�
// �̺�Ʈ�������� onItemClick �޼ҵ忡�� �Ѿ���� View �� ����ϱ� ���� �߰�
import android.view.View;

import android.content.Intent;

/*
 * 130816 ������ �ۼ�
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

    // ����Ʈ�信�� ����� ���ڿ� ����
    static final String[] m_item_string = new String[] {"�ٽ������ǡ� ���� �Ե����� �Ƴ��˹� ��!!", "���� �޴� ��õ",
    	"������ ģ�����̶� �Դµ�..", "�� �Ե� ���� ����Ʈ�� 500����(�ù�)", "�� �Ѥ� ���� ���� ����","���� �ƴ»�� ������ٰ�.."
    	,"���� �޴� ��õ�Ѵ� ������ �Ͼ��","�� ���� �ÿ�...","���� �Ǽ��� �׳� �Ե����ƿ��� ...","�� �ܹ��� �巴�� ������!!!"
    	,"�Ե� ����ŷ ¯¯�� ���� ��������","���� 24�ð��̶� �� ����.."};
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_list);
        // listview_item ���ҽ��� ���ڿ� ������ ������ ArrayAdapter ��ü�� �����Ѵ�.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_item_comment,
                                                                                         m_item_string);

        // ���ҽ� ���Ͽ� ���ǵ� id_list ��� ID �� ����Ʈ�並 ��´�.
        ListView list = (ListView) findViewById(R.id.id_list);
        // ����Ʈ�信 ArrayAdapter ��ü�� �����Ͽ� ����Ʈ�信 �����Ϳ� ��� ���¸� �����Ѵ�.
        list.setAdapter(adapter);
        // �����ʸ� �����Ѵ�.
        list.setOnItemClickListener(m_item_listener);
    }
}
