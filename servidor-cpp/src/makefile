CXX = g++
CXXFLAGS = -I. -I./rmi -I./servico -I./protocolo -I./modelo

SRCS = main.cpp $(wildcard rmi/*.cpp) $(wildcard servico/*.cpp) $(wildcard protocolo/*.cpp) $(wildcard modelo/*.cpp) $(wildcard skeleton/*.cpp)
OBJS = $(SRCS:.cpp=.o)
TARGET = main

$(TARGET): $(OBJS)
	$(CXX) $(OBJS) -o $(TARGET)

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(TARGET)

run: $(TARGET)
	./$(TARGET)