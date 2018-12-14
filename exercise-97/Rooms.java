public class Rooms {
  public interface Handler { void onEmpty(); }

  private final int SIZE;
  private final Object lock;
  private int currentRoom;
  private int inRoom;
  private Handler[] handlers;

  public Rooms(int m) {
    // Define a quantidade de salas disponíveis
    SIZE = m;
    // Define o "monitor"
    lock = this;
    // Sala atual, -1 para "nenhuma" e -2 para "esperando handler"
    currentRoom = -1;
    // Quantidade de threads na sala atual
    inRoom = 0;
    // Array com o handler de cada sala
    handlers = new Handler[m];
  }

  void enter(int i) {
    // Caso tentem entrar em uma sala que não existe
    if(i > SIZE || i < 0)
      throw new IndexOutOfBoundsException("Index " + i + " is out of bounds!");

    synchronized(lock) {
      try {
        // Enquanto houver uma sala sendo usada e a sala
        // atual não for a sala desejada por essa thread
        while(currentRoom != i && currentRoom != -1)
          lock.wait();

        // A sala atual é a sala desejada por essa thread
        currentRoom = i;
        // Há uma thread a mais na sala atual
        inRoom++;
      } catch(InterruptedException e){}
    }
  }

  boolean exit() {
    synchronized(lock) {
      // Há uma thread a menos na sala atual
      inRoom--;
      // Caso não haja mais threads na sala atual
      if(inRoom <= 0) {
        // Prevenindo quaisquer problemas de quantidade negativa de threads na sala
        inRoom = 0;

        // Se existe um handler definido para essa sala
        if(handlers[currentRoom] != null) {
          int previousRoom = currentRoom;
          // Todas as salas agora estão indisponíveis
          currentRoom = -2;
          // Chama o handler da última sala 'atual'
          handlers[previousRoom].onEmpty();
        }
        // Re-disponibiliza a escolha de salas
        currentRoom = -1;

        // Acorda todas as threads em espera
        lock.notifyAll();
        return true;
      }
      return false;
    }
  }

  public void setExitHandler(int i, Rooms.Handler h) {
    // Caso tentem definir handler para uma sala que não existe
    if(i > SIZE || i < 0)
      throw new IndexOutOfBoundsException("Index " + i + " is out of bounds!");

    // Define o handler da sala i na posição i do array
    handlers[i] = h;
  }
}
