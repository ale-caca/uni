package dom;

import com.sun.jdi.Value;

import java.util.*;

public class MyMap {
    private Map<Integer, List<Student>> map;
    static class GradeComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return Integer.compare(o2,o1);
        }
    }
    public MyMap(){
        map=new TreeMap<>(new GradeComparator());
    }


    public void add(Student student)
    {
        int key=Math.round(student.getMedia());
        List<Student> list=null;
        if(!map.containsKey(key)){
            list=new ArrayList<>();
            list.add(student);
        }else{
            list=map.get(key);
            list.add(student);
        }
        map.put(key,list);


    }
    //lipseste ceva
    public


    {

    }
}
