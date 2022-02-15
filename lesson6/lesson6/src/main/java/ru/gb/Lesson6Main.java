package ru.gb;

import java.io.IOException;
import java.util.Arrays;

public class Lesson6Main {
    public static int[] arrAfter4(int[] arr){
        int arrLength = 0;
        int arrIndexOf4 = 0;
        for (int i = arr.length-1; i >=0 ; i--) {
            if(arr[i]==4) {
            arrIndexOf4=i+1;
                break;
            }
            arrLength++;
        }
        int [] newArr = new int[arrLength];
        for (int i = arr.length-1; i >= arrIndexOf4 ; i--) {
            newArr[i-arrIndexOf4] = arr[i];
        }
        return  newArr;
    }

    public int[] arrAfter4Var2(int[] arr){
        for (int i = arr.length-1; i >= 0 ; i--) {
            if(arr[i]==4) {
                return Arrays.copyOfRange(arr, i+1, arr.length);
            }
        }
       if(!Arrays.asList(arr).contains(4)){
           throw new RuntimeException();
       }
        return new int[0];
    }

    public boolean arr1And4(int[] arr){
        int count1 = 0;
        int count4 = 0;
        for (int j : arr) {
            if (j != 4 && j != 1) {
                return false;
            }
            if (j == 4) {
                count4++;
            }
            if (j == 1) {
                count1++;
            }
        }
        return count1>0&&count4>0;
    }

    public static void main(String[] args) {

    }

}
