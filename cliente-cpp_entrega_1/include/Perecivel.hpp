#ifndef PERECIVEL_HPP
#define PERECIVEL_HPP
#include <string>

class Perecivel
{
public:
    virtual const string& getDataValidade() const = 0;
    virtual bool ehVencido() const = 0;
    virtual string getRequisitoArmazenamento() const = 0;
    virtual ~Perecivel(){};
};




#endif