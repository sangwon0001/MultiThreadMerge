package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ArrayList<Integer> a = new ArrayList<>(20000001);
        try {
            Scanner scanner = new Scanner(new File("data/input20000000.txt"));
            while(scanner.hasNext()){
                a.add(scanner.nextInt());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        int[] arr = a.stream().mapToInt(i -> i).toArray();

        a.clear();

        long[] tempLong = new long[4];
        for(int i =0;i<10;i++) {
            tempLong[0] += sortWithThread(1, arr.clone());
            tempLong[1] += sortWithThread(2, arr.clone());
            tempLong[2] += sortWithThread(4, arr.clone());
            tempLong[3] += sortWithThread(8, arr.clone());
        }
        System.out.println("thread 1 : "+tempLong[0]/10);
        System.out.println("thread 2 : "+tempLong[1]/10);
        System.out.println("thread 4 : "+tempLong[2]/10);
        System.out.println("thread 8 : "+tempLong[3]/10);
    }
    static long sortWithThread(int threadNum,int[] arr){

        ArrayList<MergeSortThread> mergeSortThreads;
        long start,end;
        start=System.currentTimeMillis();

        mergeSortThreads = seperateToThreads(arr,threadNum);

        for(MergeSortThread thread:mergeSortThreads){
            thread.start();
        }
        for(MergeSortThread thread : mergeSortThreads) {
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int[] temp = threadMerge(mergeSortThreads);

        end=System.currentTimeMillis();

        System.out.println("\n"+temp[0]+" "+temp[temp.length-1]);
        System.out.println("thread : "+threadNum+"|| time : "+(end-start));
        return end-start;
    }
     static ArrayList<MergeSortThread> seperateToThreads(int[] originlist, int threadNum){
        ArrayList<MergeSortThread> temp = new ArrayList<>();
        int i;
        for(i = 0;i<threadNum-1;i++) {
            temp.add(new MergeSortThread(Arrays.copyOfRange(originlist,i*(originlist.length/threadNum),(i+1)*(originlist.length/threadNum))));
        }
        temp.add(new MergeSortThread(Arrays.copyOfRange(originlist,i*(originlist.length/threadNum),originlist.length)));
        return temp;
    }
     static int[] threadMerge(ArrayList<MergeSortThread> threads){
         if(threads.size()==8){
             return merge(
                     merge(
                             merge(threads.get(0).getList(),threads.get(1).getList()),
                             merge(threads.get(2).getList(),threads.get(3).getList())
                     ),merge(
                             merge(threads.get(4).getList(),threads.get(5).getList()),
                             merge(threads.get(6).getList(),threads.get(7).getList())
                     )
             );
         }else if(threads.size()==4){
            return merge(
                    merge(threads.get(0).getList(),threads.get(1).getList()),
                    merge(threads.get(2).getList(),threads.get(3).getList())
                    );
        }else if(threads.size()==2){
            return merge(threads.get(0).getList(),threads.get(1).getList());
        }else if(threads.size()==1){
            return threads.get(0).getList();
        }
        return null;
    }
     static int[] merge(int[] left,int[]right){
        int[] temp = new int[left.length+right.length];
        int leftIdx=0,rightIdx=0;
        int x;
        for( x =0;x<temp.length;x++){
            if(leftIdx<left.length&&rightIdx<right.length){
                if(left[leftIdx]<right[rightIdx]){
                    temp[x]=left[leftIdx];
                    leftIdx++;
                }else{
                    temp[x]=right[rightIdx];
                    rightIdx++;
                }
            }else{
                break;
            }
        }
        while(leftIdx<left.length){
            temp[x]=left[leftIdx];
            leftIdx++;
            x++;
        }while(rightIdx<right.length){
            temp[x]=right[rightIdx];
            rightIdx++;
            x++;
        }
        return temp;
    }
}
class MergeSortThread extends Thread{
    private int [ ] list;
    MergeSortThread(int [ ] list){
        this.list = list;
    }
    @Override
    public void run() {
        super.run();
        MyMergeSort.mergeSort(list);
    }

    public int[] getList() {
        return list;
    }
}