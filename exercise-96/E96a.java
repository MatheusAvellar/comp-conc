import java.util.concurrent.locks.*;

public class E96a {	
	Lock lock;
	Condition condition;
	int male;
	int female;
	
	public bathroom(){
		lock = new ReentrantLock();
		condition = lock.newCondition();
		male = 0;
		female = 0;
	} 

	public enterMale(){
		try{
			lock.lock();
			try{
				while(female>0)
					condition.await();
			}
			catch(Exception e){}
			male++;
			System.out.println("Male= " + male);
		}
		finally{
			lock.unlock();
		}
	}
	
	//Mesma coisa pra female
	public enterFemale(){
		try{
			lock.lock();
			try{
				while(male>0)
					condition.await();
			}catch(Exception e){}
			female++;
			System.out.println("female= " + female);
		}
		finally{
			lock.unlock();
		}
	}
	
	public void leaveMale(){
		try{
			lck.lock();
			male--;
			condition.signalAll();
		}
		finally{
			lock.unlock();
		}
	}
	
	
	//Mesma coisa pra female
	public void leaveFemale(){
		try{
			lck.lock();
			female--;
			conditional.signalAll();
		}
		finally{
			lock.unlock();
		}
	}
	
}

