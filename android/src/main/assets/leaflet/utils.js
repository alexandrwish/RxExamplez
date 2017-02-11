String.prototype.hashCode = function () {
    var hash = 0, ch;
    if (this.length == 0) return hash;
    for (var i = 0; i < this.length; i++) {
        ch = this.charCodeAt(i);
        hash = ((hash << 5) - hash) + ch;
        hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
};
L.Map.addInitHook(function () {
    L.control.scale().addTo(this);
});
L.StoreTileLayer = L.TileLayer.extend({
    _loadTile: function (tile, tilePoint) {
        tile._layer = this;
        tile.onerror = this._tileOnError;
        this._adjustTilePoint(tilePoint);
        var src = this.getTileUrl(tilePoint),
            scope = this;
        if (!src) return;
        if (!window.callbacks) {
            window.callbacks = {};
        }
        if (!window.callbacks[src.hashCode()]) {
            window.callbacks[src.hashCode()] = function (tileUrlOnDevice) {
                var tiles = window.callbacks[src.hashCode()].tiles;
                if (tiles) {
                    for (var i = 0; i < tiles.length; i++) {
                        var tile = tiles[i];
                        if (tileUrlOnDevice) {
                            tile.onload = scope._tileOnLoad;
                            tile.src = tileUrlOnDevice;
                        } else {
                            scope._addTileLoadedCallback(scope, src, tile, scope.getName(), tilePoint);
                            tile.src = src;
                        }
                        tile._layer.setZIndex(0);
                        tile._layer.fire('tileloadstart', {
                            tile: tile,
                            url: tile.src
                        });
                    }
                }
                delete window.callbacks[src.hashCode()];
            };
            window.mapletJS.getTileInCache(src, this.getName(), tilePoint.x, tilePoint.y, tilePoint.z);
            window.callbacks[src.hashCode()].tiles = [];
        }
        window.callbacks[src.hashCode()].tiles.push(tile);
    },

    _addTileLoadedCallback: function (scope, src, tile, layerName, tilePoint) {
        tile.onload = function () {
            var canvas = document.createElement("canvas");
            canvas.width = this.width;
            canvas.height = this.height;
            var ctx = canvas.getContext("2d");
            ctx.drawImage(this, 0, 0);
            window.mapletJS.saveTile(canvas.toDataURL("image/png").replace(/^data:image\/(png|jpg);base64,/, ""), layerName, tilePoint.x, tilePoint.y, tilePoint.z);
            scope._tileOnLoad.apply(this);
        }
    }
});
L.TileLayer.include({
    getCRS: function () {
        if (this.options._crs) {
            return this.options._crs;
        }
        return L.CRS.EPSG3857;
    },
    getZoomOffset: function () {
        if (!('maxZoom' in this.options)) return 0;
        return 18 - this.options.maxZoom;
    },
    setName: function (name) {
        this.options._name = name;
    },
    getName: function () {
        return this.options._name || null;
    }
});
L.Map.include({
    _baseLayers: {
        openstreetmap: function () {
            return L.TileLayer.openstreetmap.apply(this, arguments);
        },
        geobase: function () {
            return L.TileLayer.geobase.apply(this, arguments);
        },
        google: function () {
            return L.TileLayer.google.apply(this, arguments);
        },
        mapquest: function () {
            return L.TileLayer.mapQuest.apply(this, arguments);
        },
        yandex: function () {
            return L.TileLayer.yandex.apply(this, arguments);
        },
        nokiahere: function () {
            return L.TileLayer.nokiaHere.apply(this, arguments);
        },
        decarta: function () {
            return L.TileLayer.decarta.apply(this, arguments);
        },
        geoinformsputnik: function () {
            return L.TileLayer.geoinformsputnik.apply(this, arguments);
        }
    },
    translate: function (key) {
        return key;
    },
    setConfig: function (config, map) {
        for (var provider in config) {
            (map._baseLayers[provider](config[provider])).addTo(map);
        }
        return this;
    }
});
L.Util.geoRouter = function (locations, callback) {
    var url = "http://router.project-osrm.org/viaroute?{0}";
    if (!locations || !locations.length) {
        return;
    }
    var params = [];
    for (var i = 0; i < locations.length; i++) {
        params.push("loc=" + locations[i].lat + "," + locations[i].lng);
    }
    this.query = url.replace("{0}", params.join('&'));
    $.ajax({
        url: this.query,
        dataType: "json",
        success: function (data) {
            if (data.route_geometry.length) {
                callback(L.Util.decodeRouteGeometry(data.route_geometry, 6));
            } else {
                callback(null);
            }
        },
        error: function () {
        }
    });
};
L.Util.decodeRouteGeometry = function (encoded, precision) {
    precision = Math.pow(10, -precision);
    var len = encoded.length, index = 0, lat = 0, lng = 0, array = [];
    while (index < len) {
        var b, shift = 0, result = 0;
        do {
            b = encoded.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        var dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
        lat += dlat;
        shift = 0;
        result = 0;
        do {
            b = encoded.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        var dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
        lng += dlng;
        array.push([lat * precision, lng * precision]);
    }
    return array;
};