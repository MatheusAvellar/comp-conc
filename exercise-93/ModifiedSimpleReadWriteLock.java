import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;

public class ModifiedSimpleReadWriteLock implements ReadWriteLock {

  int readers;
  boolean writer;
  Lock readLock, writeLock;
  // Não precisamos de ReentrantLock ou de Condition
  private final Object lock;

  public ModifiedSimpleReadWriteLock() {
    writer = false;
    readers = 0;
    readLock = new ReadLock();
    writeLock = new WriteLock();

    // Criamos uma referência à classe para usar como "monitor"
    lock = this;
  }

  public Lock readLock() { return readLock; }
  public Lock writeLock() { return writeLock; }

  class ReadLock implements Lock {
    public void lock() {
      synchronized(lock) {
        try {
          // Enquanto houver um escritor, leitores esperam
          while(writer) lock.wait();
          // Há agora mais um leitor
          readers++;
        } catch(InterruptedException e){}
      }
    }

    public void unlock() {
      synchronized(lock) {
        // Quando o leitor terminou, há um leitor a menos
        readers--;
        if(readers <= 0)
          // Se não há mais leitores, podemos acordar os escritores
          lock.notifyAll();
      }
    }


    //// Métodos necessários por ser uma classe que implementa Lock ////
    public Condition newCondition() { return null; }                 ///
    public boolean tryLock() { return false; }                       ///
    public boolean tryLock(long a, TimeUnit b) { return false; }     ///
    public void lockInterruptibly() {}                               ///
    ////////////////////////////////////////////////////////////////////
  }

  protected class WriteLock implements Lock {
    public void lock() {
      synchronized(lock) {
        try {
          // Enquanto houver outro escritor ou houver leitores,
          // escritores esperam
          while(writer || readers > 0) lock.wait();
          // Há agora um escritor
          writer = true;
        } catch(InterruptedException e){}
      }
    }
    public void unlock() {
      synchronized(lock) {
        // Quando o escritor terminou, não há mais um escritor
        writer = false;
        // Podemos acordar possíveis escritores e leitores em espera
        lock.notifyAll();
      }
    }


    //// Métodos necessários por ser uma classe que implementa Lock ////
    public Condition newCondition() { return null; }                 ///
    public boolean tryLock() { return false; }                       ///
    public boolean tryLock(long a, TimeUnit b) { return false; }     ///
    public void lockInterruptibly() {}                               ///
    ////////////////////////////////////////////////////////////////////
  }
}