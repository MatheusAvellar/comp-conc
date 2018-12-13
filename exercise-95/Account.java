import java.util.concurrent.locks.*;

public class Account {

  private int balance;
  private int preferredCount;
  private ReentrantLock lock;
  private Condition condition;
  private final Object accLock;

  public Account(int balance) {
    this.balance = balance;
    lock = new ReentrantLock();
    condition = lock.newCondition();

    // Criamos uma referência à classe para usar como "monitor"
    accLock = this;
  }

  public void deposit(int k) {
    lock.lock();
    try {
      // Adicionamos o valor recebido à conta
      balance += k;
      // Avisamos outras threads que possivelmente estavam esperando mais
      // dinheiro ser adicionado à conta
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public void withdraw(int k, boolean isPreferred) {
    lock.lock();
    try {
      synchronized(accLock) {
        // Se a retirada for preferencial, incrementamos o número de retiradas
        // 'preferenciais' em espera
        if(isPreferred) preferredCount++;
      }

      // Se a conta não possuir dinheiro suficiente,
      // ou a retirada for 'ordinária' enquanto há retiradas 'preferenciais' em
      // espera, essa operação irá esperar
      while(balance < k || (!isPreferred && preferredCount > 0))
        condition.await();

      // Se chegamos a esse ponto, então a conta possui dinheiro suficiente e
      // é a vez dessa operação executar; retiramos então o dinheiro requerido
      // da conta
      balance -= k;
    } catch(InterruptedException e) {
    } finally {
      synchronized(accLock) {
        // Se a retirada for preferencial, decrementamos o número de retiradas
        // 'preferenciais' em espera
        if(isPreferred) preferredCount--;
      }
      lock.unlock();
    }
  }
}