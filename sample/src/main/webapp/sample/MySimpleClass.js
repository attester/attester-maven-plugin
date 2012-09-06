Aria.classDefinition({
    $classpath : 'sample.MySimpleClass',
    $singleton : true,
    $prototype : {
        add : function (x, y) {
            return x + y;
        }
    }
});