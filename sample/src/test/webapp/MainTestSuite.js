Aria.classDefinition({
    $classpath : 'MainTestSuite',
    $extends : 'aria.jsunit.TestSuite',
    $constructor : function () {
        this.$TestSuite.constructor.call(this);
        this.addTests('test.sample.MySimpleClassTest');
    }
});