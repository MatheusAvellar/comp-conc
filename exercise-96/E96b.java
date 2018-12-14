import java.util.concurrent.locks.*;

public class E96b{	
	Lock lock;
	Condition condition;
	volatile int male;
	volatile int female;
	volatile boolean bool;
	
	public bathroom(){
		bool = false;
		lock = new ReentrantLock();
		male = 0;
		female = 0;
	}
	
	public void enterMale(){
		synchronized(lock){
			try{
				while((female>0) && bool){
					lock.wait();
				}
			}
			finally{
				bool = true; 
				male++;
			}
		}
	}
	
	//mesmo pra female
	public void enterFemale(){
		synchronized(lock){
			try{
				while((male>0) && bool){
					lock.wait();
				}
			}
			finally{
				bool = true; 
				female++;
			}
		}
	}
	
	
	public void leaveMale(){
		synchronized (lock){
			try{
				areYouThere = false;
				male--;
				lock.notifyAll();
			}
			catch (Exception e){}
		}
    }
	
	
	
	//mesmo pra female
	public void leaveFemale(){
		synchronized (lock){
			try{
				bool = false;
				male--;
				lock.notifyAll();
			}
			catch (Exception e){}
		}
    }
}

