Aria.classDefinition({
    $classpath : 'test.sample.MySimpleClassTest',
    $extends : 'aria.jsunit.TestCase',
    $dependencies : ['sample.MySimpleClass'],
    $prototype : {
        testAdd : function () {
            var mySimpleClass = sample.MySimpleClass;
            this.assertEquals(mySimpleClass.add(1, 2), 3, "add returned something wrong");
        }
    }
});