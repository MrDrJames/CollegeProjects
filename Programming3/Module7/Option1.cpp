#include <fstream>
#include <iostream>
// #include <string>
// This adds the reverse function I use
#include <algorithm>
using namespace std;
void addToFile(string filename);
void reverseFile(string filename);
int main()
{
    const string filename = "CSC450_CT5_mod5.txt";
    addToFile(filename);
    reverseFile(filename);
}
void addToFile(string filename)
{
    // ios::app is a flag that means append, I think it actually just puts the cursor at the end but I'm unsure
    fstream file(filename, ios::app);
    if (!file.is_open())
    {
        cout << "Wrong File" << endl;
        // returns just so the program can't mess things up if it doesn't open a file
        return;
    }
    string input;
    cout << "Give me some input data:" << endl;
    // cin >> input;
    getline(cin, input);
    file << input;
    cout << "Appended to file" << endl;
    file.close();
}
void reverseFile(string filename)
{
    // No app here but ios::in means reading mode so there is less risk of messing up the file
    fstream file(filename, ios::in);
    if (!file.is_open())
    {
        cout << "Wrong File" << endl;
        // Same as the first method
        return;
    }
    cout << "Reverse reverse" << endl;
    string temp, line;
    // I found this code online but it sure is neat, getline assigns each line of the file line by line to temp
    //  and returns a reference that can be checked like a boolean to see if there are more lines
    // I would prefer to start at the end of the file, read line, reverse it, and then write it to the output file
    // but I cannot figure out how to do that, reading the file in reverse is beyond me for now
    while (getline(file, temp))
    {
        line = line + temp + "\n";
        // "\n" is added to maintain line breaks because getline doesn't read them
    }
    file.close();
    reverse(line.begin(), line.end());
    // This makes the reverse file, the out flag makes it create a new file
    file.open("CSC450-mod5-reverse.txt",fstream::out);
    file << line << endl;
    cout << "Done reverse reverseing";
    file.close();
}