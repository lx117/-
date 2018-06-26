package jianrt.slidetounlock.net;

import android.os.AsyncTask;


/** 
 *  
 * @Description：异步数据加载器 
 */  
public class AsyncLoader<P, S, T> extends AsyncTask<P, S, T> {  
  
    private OnLoadListener<P, S, T> mListener;  
    private int mDataType;  
    private Exception ex;
    private static AsyncLoader instance;
    public static synchronized AsyncLoader getInstance() {
        if (instance == null) {
            instance = new AsyncLoader(1);
        }
        return instance;
    }
    public AsyncLoader(int dataType) {  
        mDataType = dataType;  
  
        mListener = new OnLoadListener<P, S, T>() {  
            @Override  
            public void onDataStart() {  
            }  
  
            @Override  
            public T doInWorkerThread(int dataType, P... params)  
                    throws Exception {  
                return null;  
            }  
              
            @Override  
            public void onDataGet(T result) {  
            }  
  
            @Override  
            public void onDataFail(Exception e) {  
            }  
  
            @Override  
            public void onDataFinish() {  
            }  

            @Override
            public void onDataProgress(S... values) {
            }
              
        };  
    }
    public static void addToRequestQueue(OnLoadListener mListener,Object... parm){
        getInstance().setOnLoadListener(mListener);
        getInstance().execute(parm);
    }
    public void setOnLoadListener(OnLoadListener<P, S, T> listener) {  
        if (listener != null) {  
            mListener = listener;  
        }  
    }  
  
    @Override  
    protected void onPreExecute() {  
        super.onPreExecute();  
        mListener.onDataStart();  
    }  
  
    @Override  
    protected T doInBackground(P... params) {  
        try {  
            return (T) mListener.doInWorkerThread(mDataType,params);  
        } catch (Exception e) {  
            e.printStackTrace();  
            ex = e;  
            return null;  
        }  
    }  
      
    @Override  
    protected void onProgressUpdate(S... values) {  
        super.onProgressUpdate(values);  
          
    }  
  
    @Override  
    protected void onPostExecute(T result) {  
        super.onPostExecute(result);  
        if (ex != null) {  
            mListener.onDataFail(ex);  
        } else {  
            mListener.onDataGet(result);  
        }  
        mListener.onDataFinish();  
    }  
  
    @Override  
    protected void onCancelled() {  
        super.onCancelled();  
        mListener.onDataFinish();  
    }  
  
    /** 
     *  
     * @Description：异步数据回调接口类 
     */  
    public interface OnLoadListener<P, S, T> {

        /**
         *
         * @Description：异步数据加载开始时回调
         *
         *
         */
        void onDataStart();

        /** 
         *  
         * @Description：异步数据请求回调，运行子线程中 
         * 
         * @param dataType 
         * @return 
         * @throws Exception 
         *  
         */
        T doInWorkerThread(int dataType, P... params) throws Exception;
          
        /** 
         *  
         * @Description：异步数据进行的进度回调，用来显示进度条或更新UI等 
         * 
         * @param values 
         * @return 
         * @throws Exception 
         *  
         */
        void onDataProgress(S... values);
  
        /** 
         *  
         * @Description：异步数据返回时回调 
         * 
         * @param result 
         * void 
         */
        void onDataGet(T result);
  
        /** 
         *  
         * @Description：异步数据请求失败时回调 
         * 
         * @param e 
         * void 
         */
        void onDataFail(Exception e);
  
        /** 
         *  
         * @Description：异步数据结束时回调 
         * 
         * void 
         */
        void onDataFinish();

    }  
  
}  
