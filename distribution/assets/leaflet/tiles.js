L.TileLayer.Decarta = L.StoreTileLayer.extend({
    getTileUrl: function (tilePoint) {
        var offset = Math.pow(2, tilePoint.z - 1);
        return L.Util.template(this._url, L.extend({
            s: this._getSubdomain(tilePoint),
            z: tilePoint.z,
            x: tilePoint.x - offset,
            y: -tilePoint.y + offset - 1
        }, this.options));
    }
});
L.TileLayer.decarta = function (options) {
    var url = "http://vip5dzsv-0{s}.decartahws.com/openls/image-cache/TILE/{x}/{y}/{z}?CLIENTNAME=map-sample-app&CONFIG=global-decarta&P=EPSG:3857",
        defaultOptions = {
            _name: "decarta",
            subdomains: '123',
            minZoom: 1,
            maxZoom: 18,
            noWrap: true,
            attribution: '&copy; decarta'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.TileLayer.Decarta(url, defaultOptions);
};
L.TileLayer.Geobase = L.StoreTileLayer.extend({
    getTileUrl: function (tilePoint) {
        var offset = Math.pow(2, tilePoint.z - 1);
        return L.Util.template(this._url, L.extend({
            s: this._getSubdomain(tilePoint),
            z: Math.pow(2, tilePoint.z - 2),
            x: tilePoint.x - offset,
            y: tilePoint.y - offset,
            n: 4
        }, this.options));
    }
});
L.TileLayer.geobase = function (options) {
    var url = 'http://{server_ip}/GeoStream/tile.aspx?t={x},{y},256,{n},{z}',
        defaultOptions = {
            _name: "geobase",
            minZoom: 2,
            maxZoom: 18,
            noWrap: true,
            attribution: '&copy; geobase'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.TileLayer.Geobase(url, defaultOptions);
};
L.TileLayer.geoinformsputnik = function (options) {
    var config = JSON.parse(options.custom_map_config),
        url = config.url,
        defaultOptions = {
            _name: "geoinformsputnik",
            width: 300,
            height: 300,
            tileSize: 300,
            format: 'image/png',
            layers: '',
            transparent: 1,
            maxZoom: 12,
            minZoom: 1,
            crs: L.CRS.EPSG4326 // wgs84 as default
        };
    if (config.projection) {
        try {
            config.crs = new L.Proj.CRS(config.projection.code, config.projection.defs, {
                origin: [9400000, 6200000],
                resolutions: [5600, 2800, 1400, 560, 280, 140, 56, 28, 14, 5.6, 2.8, 1.4, 0.55, 0.2]
            });
            defaultOptions._crs = config.crs;
        } catch (e) {
            config.crs = L.CRS.EPSG4326;
        }
    }
    for (var option in config) {
        if (option in defaultOptions) {
            defaultOptions[option] = config[option];
        }
    }
    return new L.TileLayer.WMS(url, defaultOptions);
};
L.TileLayer.google = function (options) {
    var url = 'http://mt{s}.googleapis.com/vt/?hl=ru&src=api&x={x}&y={y}&z={z}&s=',
        defaultOptions = {
            _name: "google",
            subdomains: '123',
            noWrap: true,
            attribution: '&copy; <a href="http://google.com">Google Inc</a>'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.StoreTileLayer(url, defaultOptions);
};
L.TileLayer.mapQuest = function (options) {
    var url = 'http://otile{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png',
        defaultOptions = {
            _name: "mapQuest",
            subdomains: '1234',
            noWrap: true,
            attribution: 'Data CC-By-SA by <a href="http://openstreetmap.org/" target="_blank">OpenStreetMap</a>, ' +
            'Tiles Courtesy of <a href="http://open.mapquest.com" target="_blank">MapQuest</a>'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.StoreTileLayer(url, defaultOptions);
};
L.TileLayer.nokiaHere = function (options) {
    var url = 'http://{s}.maps.nlp.nokia.com/maptile/2.1/maptile/newest/normal.day/{z}/{x}/{y}/256/png8?lg={lang}&app_id={map_id}&token={map_key}',
        defaultOptions = {
            _name: "nokiaHere",
            subdomains: '1234',
            noWrap: true,
            attribution: '&copy; <a href="http://here.com">Nokia</a>',
            lang: 'en'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.StoreTileLayer(url, defaultOptions);
};
L.TileLayer.OpenStreetMap = L.StoreTileLayer.extend({
    getTileUrl: function (tilePoint) {
        var domain = this.options.domains[~~(Math.random() * 10) % this.options.domains.length];
        return L.Util.template(this._url, L.extend({
            s: this._getSubdomain(tilePoint),
            z: tilePoint.z,
            x: tilePoint.x,
            y: tilePoint.y,
            d: domain
        }, this.options));
    }
});
L.TileLayer.openstreetmap = function (options) {
    var url = 'http://{s}.tile.{d}/{z}/{x}/{y}.png',
        defaultOptions = {
            _name: "openstreetmap",
            useAjax: false,
            subdomains: 'abc',
            noWrap: true,
            domains: ['osm.org', 'openstreetmap.org'],
            attribution: '&copy; <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a> contributors'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.TileLayer.OpenStreetMap(url, defaultOptions);
};
L.TileLayer.yahoo = function (options) {
    var url = 'http://{s}.maps.nlp.nokia.com/maptile/2.1/maptile/b6d442adca/normal.day/{z}/{x}/{y}/256/png8?lg={lang}&token=',
        defaultOptions = {
            _name: "yahoo",
            minZoom: 0,
            maxZoom: 17,
            subdomains: '123',
            noWrap: true,
            attribution: '&copy; Yahoo',
            lang: 'ENG'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.StoreTileLayer(url, defaultOptions);
};
L.TileLayer.yahooTraffic = function (options) {
    var url = 'http://lbs.ovi.com/traffic/6.0/tiles/{z}/{x}/{y}/256/png32?token=&compress=true',
        defaultOptions = {
            _name: "yahooTraffic",
            minZoom: 0,
            maxZoom: 17,
            noWrap: true,
            attribution: '&copy; Yahoo traffic',
            lang: 'ENG'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.StoreTileLayer(url, defaultOptions);
};
L.TileLayer.yandex = function (options) {
    var url = 'http://{type}0{s}.maps.yandex.ru/tiles?l=map&v={version}&x={x}&y={y}&z={z}&lang={lang}',
        defaultOptions = {
            _name: "yandex",
            _crs: L.CRS.EPSG3395,
            version: '4.3.3',
            type: 'vec',
            lang: 'ru_RU',
            minZoom: 0,
            maxZoom: 18,
            subdomains: '123',
            noWrap: true,
            attribution: '&copy; Яндекс, &copy; Роскартография, &copy; ЗАО «Резидент», &copy; ЗАО «ТГА», 2006'
        };
    for (var option in options) {
        defaultOptions[option] = options[option];
    }
    return new L.StoreTileLayer(url, defaultOptions);
};