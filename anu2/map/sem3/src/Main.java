import dom.MyMap;
import dom.Student;

import java.util.*;



public class Main {
    public static List<Student> getList() {
        List<Student> l = new ArrayList<Student>();
        l.add(new Student("Ana", 9.7f));
        l.add(new Student("Dan", 7.3f));
        l.add(new Student("Tom", 6f));
        l.add(new Student("Ale", 6.9f));
        l.add(new Student("Gogi", 9.5f));
        l.add(new Student("Antonio", 9.9f));
        return l;
    }

    public static void main(String[] args) {
        Student s1= new Student("Dan", 4.5f);
        Student s2 = new Student("Ana", 8.5f);
        Student s3 = new Student("Dan", 4.5f);
        System.out.println(s1.equals(s3));
        System.out.println(s1.hashCode());
        System.out.println(s3.hashCode());

        System.out.println("hashSet");

        Set<Student> student = new HashSet<Student>();

        student.add(s1);
        student.add(s2);
        student.add(s3);
        System.out.println(student.size());

        System.out.println("tree Set");
        Set<Student>treeSet=new TreeSet<>();
        treeSet.add(s1);
        treeSet.add(s2);
        treeSet.add(s3);
        for(Student s: treeSet){
            System.out.println(s);
        }

        System.out.println("tree Set with comaprator");
        Comparator<Student> comparator = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return Float.compare(o2.getMedia(),o1.getMedia());
            }
        };
        Set<Student> setWithComparator=new TreeSet<>(comparator);
        setWithComparator.add(s1);
        setWithComparator.add(s2);
        setWithComparator.add(s3);
        for(Student s: setWithComparator){
            System.out.println(s);
        }

        System.out.println("HashMap");
        Map<String,Student> map=new HashMap<>();
        map.put(s1.getNume(),s1);
        map.put(s2.getNume(),s2);
        map.put(s3.getNume(),s3);

        System.out.println(map.size());
        for(Map.Entry<String,Student> entry:map.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        System.out.println("trree map");
        Map<String,Student> map2=new TreeMap<>();
        map2.put(s1.getNume(),s1);
        map2.put(s2.getNume(),s2);
        map2.put(s3.getNume(),s3);
        for(Map.Entry<String,Student> entry:map2.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        System.out.println("List");
        List<Student> list=new ArrayList<>();
        list.add(s1);
        list.add(s2);
        list.add(s3);
        Iterator<Student> iterator=list.iterator();

        while(iterator.hasNext()){
            Student s= iterator.next();
            System.out.println(s);
        }

        MyMap<Student> =getList();

        List<Student> list2=getList();
    }
}