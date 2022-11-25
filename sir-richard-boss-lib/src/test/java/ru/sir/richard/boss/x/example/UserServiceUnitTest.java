package ru.sir.richard.boss.x.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.sir.richard.boss.dao.WikiDao;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
	
	@Mock
	private WikiDao wikiDao;
	
	@Mock
    private List<String> mockedList;

    @Test
    public void shouldDoSomething() {
    	
    	mockedList.add("one");
    	
    	Mockito.when(mockedList.size()).thenReturn(1);
    	Mockito.verify(mockedList).add("one");
        assertEquals(1, mockedList.size());
    	
    	/*
    	mockedList.add("one");
        Mockito.verify(mockedList).add("one");
        assertEquals(0, mockedList.size());

        Mockito.when(mockedList.size()).thenReturn(100);
        assertEquals(100, mockedList.size());
        */
        
        
    }

}
