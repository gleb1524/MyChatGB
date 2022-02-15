
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.gb.Lesson6Main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Lesson6Test {

    Lesson6Main lesson6Main;

    @BeforeEach
    public void init(){
        lesson6Main = new Lesson6Main();
    }

    public static Stream<Arguments> testArrAfter4(){
        List<Arguments> list = new ArrayList<>();
        list.add(Arguments.arguments(new int[]{5,6,7,8,9,10}, new int[]{1,2,3,4,5,6,7,8,9,10}));
        list.add(Arguments.arguments(new int[]{1,2,3,5,6,7,8,9,10}, new int[]{4,1,2,3,5,6,7,8,9,10}));
        list.add(Arguments.arguments(new int[]{8,9,10}, new int[]{1,2,3,4,5,6,4,8,9,10}));
        list.add(Arguments.arguments(new int[]{}, new int[]{1,2,4}));
        return list.stream();
    }

    @ParameterizedTest
    @MethodSource("testArrAfter4")
    public void massTestArrAfter4(int[] result, int[] arr){
            Assertions.assertArrayEquals(result, lesson6Main.arrAfter4Var2(arr));
    }

    @Test
    public void testArrAfter4Exception(){
        Assertions.assertThrows(RuntimeException.class,()->lesson6Main.arrAfter4Var2(new int[]{1,2}));
    }

    @Test
    public void testArr1And4(){
        Assertions.assertTrue(lesson6Main.arr1And4(new int[]{1,1,4}));
    }

    public static Stream<Arguments> arr1And4Parameters(){
        List<Arguments> list = new ArrayList<>();
        list.add(Arguments.arguments(false, new int[]{1,1,1,1,1}));
        list.add(Arguments.arguments(false, new int[]{4,4,4,4,4}));
        list.add(Arguments.arguments(false, new int[]{0,1}));
        list.add(Arguments.arguments(false, new int[]{}));

        return list.stream();
    }

    @ParameterizedTest
    @MethodSource("arr1And4Parameters")
    public void massTestArr1and4(boolean result, int[] arr){
        Assertions.assertFalse(lesson6Main.arr1And4(arr));
    }


}
