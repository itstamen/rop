window.addEvent('domready', function () {
    var boxAnimation = new Fx.Tween($('box'));
    boxAnimation.start('margin-left', 150)
        .chain(function () {
            boxAnimation.start('width', 100);
        })
        .chain(function () {
            boxAnimation.start('opacity', 0);
        })
        .chain(function () {
            boxAnimation.start('opacity', 100);
        })
        .chain(function () {
            boxAnimation.start('margin-left', 0);
        });
});