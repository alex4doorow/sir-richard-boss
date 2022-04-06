package ru.sir.richard.boss;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.sir.richard.boss.api.cdek.CdekApiTest;
import ru.sir.richard.boss.model.calc.OrderTotalAmountsCalculatorTest;
import ru.sir.richard.boss.model.data.CloneableTest;
import ru.sir.richard.boss.model.data.OrderTest;
import ru.sir.richard.boss.model.utils.TextUtilsTest;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	TextUtilsTest.class, 
	CloneableTest.class,
	OrderTest.class,
	//CdekApiTest.class,
	OrderTotalAmountsCalculatorTest.class	
})
public class SirRichardBossSuite {

}