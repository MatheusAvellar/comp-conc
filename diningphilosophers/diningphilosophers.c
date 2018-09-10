/***********************************************************
 * == DERIVATIVE WORK LICENSE ==
 * This code is a modified version of the one found at
 * www.c-program-example.com. This code is subject to the
 * original license as stated below. As for the changes that
 * I did, you can use it however you wish, as long as you
 * don't hold me liable. This means that if
 * the original author gives permission for you to use his
 * code in some way, then you can also use this derivative
 * work the same way.
 *
 * == UNDERLYING WORK LICENSE ==
 * You can use all the programs on  www.c-program-example.com
 * for personal and learning purposes. For permissions to use the
 * programs for commercial purposes,
 * contact info@c-program-example.com
 * To find more C programs, do visit www.c-program-example.com
 * and browse!
 * 
 *                      Happy Coding
 ***********************************************************/

// SOLUTION:
// Nessa solução, cada filósofo sempre tenta pegar o menor
// talher. Após pegar o menor, tenta pegar o maior. Isso
// elimina deadlocks porque o filósofo 1 e o filósofo N-1
// compartilham o mesmo menor talher. Desse jeito, é
// impossível os filósofos ficarem travados caso todos peguem
// o menor talher disponível: no caso onde N-1 dos N filósofos
// pegam o menor talher simultaneamente, o talher de maior
// número está sempre disponível, então um filósofo vai ter
// 2 talheres pra comer.

#include <stdio.h>
#include <semaphore.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>

#define N 5 // number of philosophers

#define SMALL	((ph_num + 0) % N)
#define BIG		((ph_num + 1) % N)

sem_t chopsticks[N]; // binary semaphores controling the access to each chopstick

extern int errno;

void *philosopher(void *num);
void take_chopstick(int);
void put_chopstick(int);
void test(int);

int phil_num[N];

#define ANSI_COLOR_RED     "\x1b[31m"
#define ANSI_COLOR_GREEN   "\x1b[32m"
#define ANSI_COLOR_YELLOW  "\x1b[33m"
#define ANSI_COLOR_BLUE    "\x1b[34m"
#define ANSI_COLOR_MAGENTA "\x1b[35m"
#define ANSI_COLOR_CYAN    "\x1b[36m"
#define ANSI_COLOR_RESET   "\x1b[0m"

int main()
{
    int ph_num;
    for (ph_num = 0; ph_num < N; ph_num++) {
        printf("Philosopher %d needs chopsticks %d and %d\n", ph_num + 1, SMALL + 1, BIG + 1);
    }

    printf("Dining Philosophers: working example 1\n");

    int i, retval;
    pthread_t thread_id[N]; // each thread will simulate the behavior of one philosopher

    for (i = 0; i < N; i++)
    {
        retval = sem_init(&chopsticks[i], 0, 1);
        printf("(%d %d)\n", retval, errno);
    }

    for (i = 0; i < N; i++)
    {
        phil_num[i] = (N - i + 1) % N; // shuffling philosophers creation order
        printf("Created philosopher %d\n", phil_num[i] + 1);
        pthread_create(&thread_id[i], NULL, philosopher, &phil_num[i]);
    }

    for (i = 0; i < N; i++)
        pthread_join(thread_id[i], NULL);
}

void *philosopher(void *num)
{
    int i = *((int*)num);
    printf("Philosopher %d is thinking\n", i + 1);
    while (1)
    {
        take_chopstick(i);
        printf(ANSI_COLOR_GREEN "Philosopher %d is eating\n" ANSI_COLOR_RESET, i + 1);
        sleep(0);
        put_chopstick(i);
    }
}

void take_chopstick(int ph_num)
{
    int retval1;
    int smaller = SMALL;
    int bigger = BIG;
    if (ph_num == N-1){
    	smaller = BIG;
    	bigger = SMALL;
    }
    // waiting for small chopstick
    printf("Philosopher %d is Hungry and waits for chopstick %d\n", ph_num + 1, smaller + 1);
    retval1 = sem_wait(&chopsticks[smaller]);
    // got small chopstick
    printf("Philosopher %d got chopstick %d (%d %d)\n", ph_num + 1, smaller + 1, retval1, errno);

    sleep(1);

    // waiting for big chopstick
    printf("Philosopher %d now waits for chopstick %d\n", ph_num + 1, bigger + 1);
    retval1 = sem_wait(&chopsticks[bigger]);
    // got even chopstick
    printf("Philosopher %d took chopstick %d and %d (%d %d)\n", ph_num + 1, smaller + 1, bigger + 1, retval1, errno);

}

void put_chopstick(int ph_num)
{
    int smaller = SMALL;
    int bigger = BIG;
    if (ph_num == N-1){
    	smaller = BIG;
    	bigger = SMALL;
    }
    printf("Philosopher %d putting chopstick %d and %d down\n", ph_num + 1, smaller + 1, bigger + 1);
    printf("Philosopher %d is thinking\n", ph_num + 1);
    sem_post(&chopsticks[smaller]);
    sleep(1);
    sem_post(&chopsticks[bigger]);
    sleep(1);
}