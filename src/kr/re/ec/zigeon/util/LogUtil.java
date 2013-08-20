package kr.re.ec.zigeon.util;

/**
 * Class LogUtil.
 * print to log using Class and Method name.
 * 130812 Kim Taehee
 * slhyvaa@nate.com
 */

public class LogUtil {
	private static boolean debug = true;// BuildConfig.DEBUG;
	private static String Tag = "User Logged";
	
	/* vervose */
	public static void v(String log)
	{
		if(debug) {
			android.util.Log.v(Tag, new Exception().getStackTrace()[1].getClassName() + "::" 
					+ new Exception().getStackTrace()[1].getMethodName() + "():" + log); //�� ���� ������ Ŭ������� �޼ҵ�� ȣ��
		}
	}

	/* debug */
	public static void d(String log)
	{
		if(debug) {
			android.util.Log.d(Tag, new Exception().getStackTrace()[1].getClassName() + "::" 
					+ new Exception().getStackTrace()[1].getMethodName() + "():" + log); //�� ���� ������ Ŭ������� �޼ҵ�� ȣ��
		}
	}
	
	/* info */
	public static void i(String log)
	{
		if(debug) {
			android.util.Log.i(Tag, new Exception().getStackTrace()[1].getClassName() + "::" 
					+ new Exception().getStackTrace()[1].getMethodName() + "():" + log); //�� ���� ������ Ŭ������� �޼ҵ�� ȣ��
		}
	}

	/* warn */
	public static void w(String log)
	{
		android.util.Log.w(Tag, new Exception().getStackTrace()[1].getClassName() + "::" 
				+ new Exception().getStackTrace()[1].getMethodName() + "():" + log); //�� ���� ������ Ŭ������� �޼ҵ�� ȣ��
	}
	
	/* error */
	public static void e(String log)
	{
		android.util.Log.e(Tag, new Exception().getStackTrace()[1].getClassName() + "::" 
				+ new Exception().getStackTrace()[1].getMethodName() + "():" + log); //�� ���� ������ Ŭ������� �޼ҵ�� ȣ��
	}
}
