#pragma once 
#include <string>

using namespace std;

class ClienteAPI {
public:
    string get(const string& url);
    string post(const string& url, const string& jsonBody);

    string del(const string& url);

};