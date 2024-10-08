package Portfolio;


class Student{
    String name;
    String address;
    double gpa;

    // Here is the normal constructor
    public Student( String name, String address,double gpa) {
        this.gpa = gpa;
        this.name = name;
        this.address = address;
    }
    @Override
    public String toString() {
        return "Name ='" + name + "'" +
        ",address='" + address + "'"+
        ",GPA='" + gpa + "'";
    }
    public int compareTo(Student that) {
        // Just outsourcing to the string compareTo method but making sure to uppercase them both first just in case
        // I say just in case but it's really just because in my testing I don't consistently capitalizing and it's annoying
        return this.name.toUpperCase().compareTo(that.name.toUpperCase());
    }

}