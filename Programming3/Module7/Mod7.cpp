#include <iostream>
#include <thread>
#include <mutex>

using namespace std;
// Gloabl variable so both threads can access it
int counter = 0;
// Since threads are sharing a counter, a lock is important
mutex counting;

void countup(){
    // Very simple counting loop, just make sure to lock and unlock at the beginning and end of each loop
    while(counter < 20) {
        counting.lock(); 
        counter++;
        std::cout << counter << endl;
        // I included a sleep_for statement mostly for the purpose of debugging as it goes too quick without it
        std::this_thread::sleep_for(std::chrono::milliseconds(300));
        counting.unlock();
    }
}

void countdown(){
    // This is being used as a continous if statement, I had a sleep_for command in it but I realized it is uncessary and wanted to clean the program up
    while(counter < 20){}
    // Same as above just in reverse
    while(counter > 0){
        counting.lock();
        counter--;
        std::cout << counter << endl;
        std::this_thread::sleep_for(std::chrono::milliseconds(300));
        counting.unlock();
    }
}

int main()
{   
    // Simply start both threads
    thread one(countup);
    thread two(countdown);
    // Can't end the program without joining the threads
    one.join();
    two.join();
}