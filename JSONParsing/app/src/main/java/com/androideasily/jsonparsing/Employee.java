package com.androideasily.jsonparsing;

import java.util.List;

/**
 * Created by Anand on 13-06-2017.
 */

class Employee {
    //Class used in parsing Json using andorid parser
    private int id, dept_code;
    private String name, address, dept_name;
    private boolean isFree;
    private List<Projects> project;

    Employee(int id, int dept_code, String name, String address, String dept_name, boolean isFree, List<Projects> project) {
        this.id = id;
        this.dept_code = dept_code;
        this.name = name;
        this.address = address;
        this.dept_name = dept_name;
        this.isFree = isFree;
        this.project = project;
    }
}
