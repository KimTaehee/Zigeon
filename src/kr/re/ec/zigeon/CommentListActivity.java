package kr.re.ec.zigeon;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.View; // �̺�Ʈ�������� onItemClick �޼ҵ忡�� �Ѿ���� View �� ����ϱ� ���� �߰�
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;  // ����Ʈ�� Ŭ������ ����ϱ� ���� �߰�
import android.widget.AdapterView.OnItemClickListener;  // ������ �̺�Ʈ�� ����ϱ� ���� �߰�
import android.widget.AdapterView;  // �̺�Ʈ�������� onItemClick �޼ҵ忡�� �Ѿ���� AdapterView �� ����ϱ� ���� �߰�
import android.widget.TextView;  // �ؽ�Ʈ�� Ŭ������ ����ϱ� ���� �߰�
import android.widget.ArrayAdapter;  // �����Ŭ������ ����ϱ� ���� �߰�


public class CommentListActivity extends Activity implements OnItemClickListener, OnClickListener
{
    // �̺�Ʈ �������� ������ �޼ҵ忡���� ����͸� ����� �� �ֵ��� ��������� �����Ѵ�.
    ArrayAdapter<String> m_adapter;   
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_list);        // ���ҽ� ������ ���̾ƿ� ������ �״�� ����Ѵ�.

        // ����Ʈ�䰡 ���� ���ڿ��� �������� �߰��ǹǷ� ArrayList Ŭ���� ��ü�� �����Ͽ� ����Ѵ�.
        ArrayList<String> list_string = new ArrayList<String>();
        // ����͸� �Ҵ��Ͽ� ArrayList ��ü�� �׸� ��� ���ҽ��� �����Ѵ�.
        m_adapter = new ArrayAdapter<String>(this, R.layout.listview_item_comment, list_string);

        // ���ҽ� ���Ͽ� ���ǵ� id_list ��� ID�� ����Ʈ�並 ���´�.
        ListView list = (ListView) findViewById(R.id.id_list);
        // ����Ʈ�信 ����͸� �����Ѵ�.
        list.setAdapter(m_adapter);
        // ����Ʈ�信 ������ �������̽��� ������ �� Ŭ������ �Ѱ��־� �����ʸ� �����Ѵ�.
        list.setOnItemClickListener(this);

        // ���ҽ� ���Ͽ� ���ǵ� id_btn �̶�� ID�� ��ư�� ���´�.
        Button btn = (Button) findViewById(R.id.id_btn);
        // ��ư�� ������ �������̽��� ������ �� Ŭ������ �Ѱ��־� �����ʸ� �����Ѵ�.
        btn.setOnClickListener(this);
    }

    // OnClickListener �������̽��� onClick �޼ҵ带 �����Ѵ�.
    public void onClick(View view)
    {
        // ����Ͱ� ���� ������(ArrayList)�� ��� �׸��� �������� ���´�.
        int string_count = m_adapter.getCount();

        // ���ҽ� ���Ͽ� ���ǵ� id_edit �̶�� ID�� ����Ʈ�ؽ�Ʈ�� ���´�.
        EditText edit = (EditText) findViewById(R.id.id_edit);
        // ����Ʈ�ؽ�Ʈ�� �Էµ� ���ڿ��� String Ÿ������ �����Ͽ� ����Ʈ���� �� ���� �߰��Ѵ�.
        m_adapter.insert(edit.getText().toString(), string_count);

        // ���ҽ� ���Ͽ� ���ǵ� id_list �̶�� ID�� ����Ʈ�並 ���´�.
        ListView list = (ListView) findViewById(R.id.id_list);  
        list.smoothScrollToPosition(string_count);    // ���ڿ��� �߰��� �׸������� ��ũ���� �̵��Ѵ�.
        edit.setText("");  // ����Ʈ�ؽ�Ʈ�� ���ڿ��� �����.
    }

    // OnItemClickListener �������̽��� onItemClick �޼ҵ带 �����Ѵ�.
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // �Ű� ������ �Ѿ�� ���õ� �׸� View �� TextView �� ĳ�����Ѵ�.
        TextView select_item = (TextView)view;
        // ���ҽ� ���Ͽ� ���ǵ� id_edit ��� ID�� ����Ʈ�ؽ�Ʈ�� ���´�.
        EditText edit = (EditText) findViewById(R.id.id_edit);
        // TextView �� ĳ���õ� ���� �׸��� ���ڿ��� �� ����Ʈ�ؽ�Ʈ�� ��½�Ų��.
        edit.setText(select_item.getText());
    }
}
