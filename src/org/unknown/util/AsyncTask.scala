package org.unknown.util

//@todo http://qa.atmarkit.co.jp/q/2352
//toco esto porque si no saila error not implemented
abstract class AsyncTask[A,B,C] extends android.os.AsyncTask[A,B,C] {
    override protected def doInBackground(values: A*): C = {
      doInBackgroundImpl(values: _*)
    }
    protected def doInBackgroundImpl(values: A*): C
    override protected def onProgressUpdate(progress: B*) = {
      onProgressUpdateImpl(progress: _*)
    }
    protected def onProgressUpdateImpl(progress: B*)
}
