L.TimeIcon = L.DivIcon.extend({
    options: {
        hr: '00',
        min: '00',
        isPU: false,
        priority: 'lightgreen'
    },
    createIcon: function () {
        var div = document.createElement('div');
        this._setIconStyles(div, 'icon');
        div.innerHTML = '<div class="inner"> <div class="container"> <span class="image" style="color:' + this.options.priority + ';">' + (this.options.isPU ? '⇧' : '⇩') + '</span> <span>' + this.options.hr + '<sup>' + this.options.min + '</sup></span> </div> </div>';
        return div;
    }
});

L.timeIcon = function (option) {
    return new L.TimeIcon(option);
};