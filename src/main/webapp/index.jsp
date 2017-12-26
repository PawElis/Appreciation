<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>LookForing</title>
    <meta http-equiv="content-type" charset="UTF-8">
    <script type="text/javascript" src="resources/js/index.js"></script>
    <script src="http://yandex.st/jquery/1.6.4/jquery.min.js"></script>
    <link rel="stylesheet" href="/resources/css/index_style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/resources/css/icon.png" type="image/x-icon"/>
    <script src="https://api-maps.yandex.ru/2.1/?load=package.standard&lang=ru_RU" type="text/javascript">
    </script>
    <script type="text/javascript">
        ymaps.ready(init);
        var myMap,

            clusterer;

        function geo() {
            var geolocation = ymaps.geolocation,
                myPlacemark = new ymaps.Placemark([geolocation().latitude, geolocation().latitude], {
                    hintContent: 'I am here!',
                    balloonContent: 'Столица России'
                }, {
                    preset: 'twirl#houseIcon'
                });
            clusterer1.add(myPlacemark);
            myMap.geoObjects.add(clusterer1);
        }

        function initgeo() {
            var geolocation = ymaps.geolocation;
            geolocation.get({
                provider: 'browser',
                mapStateAutoApply: true
            }).then(function (result) {
                // Красным цветом пометим положение, полученное через браузер.
                // Если браузер не поддерживает эту функциональность, метка не будет добавлена на карту.
                /* result.geoObjects.options.set('iconColor', '#AC646C');
                 result.geoObjects.options.set('iconColor', '#AC646C');*/
                myMap.geoObjects.add(result.geoObjects);
            });
        }

        function init() {
            myMap = new ymaps.Map("map", {
                center: [59.9868, 30.1584],
                zoom: 3,
                behaviors: ['default', 'scrollZoom']
            }, {
                searchControlProvider: 'yandex#search'
            });
            clusterer = new ymaps.Clusterer({
                preset: 'islands#invertedBlueClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            clusterer1 = new ymaps.Clusterer({
                preset: 'islands#invertedDarkorangeClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            clusterer2 = new ymaps.Clusterer({
                preset: 'islands#invertedVioletClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            clusterer3 = new ymaps.Clusterer({
                preset: 'islands#invertedDarkblueClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            clusterer4 = new ymaps.Clusterer({
                preset: 'islands#invertedOrangeClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            clusterer5 = new ymaps.Clusterer({
                preset: 'islands#twirl#invertedPinkClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            clusterer6 = new ymaps.Clusterer({
                preset: 'islands#invertedRedClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            clusterer7 = new ymaps.Clusterer({
                preset: 'islands#invertedYellowClusterIcons',
                groupByCoordinates: false,
                clusterDisableClickZoom: true,
                clusterHideIconOnBalloonOpen: false,
                geoObjectHideIconOnBalloonOpen: false
            });
            myMap.options.set({maxZoom: 14, minZoom: 3});
            clusterer.options.set({
                gridSize: 80,
                clusterDisableClickZoom: true
            });
            <c:forEach items="${places}" var="place">
            myPlacemark = new ymaps.Placemark([${place.latitude}, ${place.longitude}], {
                hintContent: 'latitude: ${place.latitude}, longitude: ${place.longitude}',
                /*balloonContent: 'Столица России'*/
            });
            clusterer.add(myPlacemark);
            </c:forEach>
            //   myMap.geoObjects.add(myPlacemark);
            myMap.geoObjects.add(clusterer);
            //Если что - отменить здесь!!!
            /* myMap.setBounds(clusterer.getBounds(), {
             checkZoomRange: true
             });*/
        }

        function placesSport() {
            clearMap();
            var myPlacemark;
            <c:forEach items="${pl0}" var="place">
            myPlacemark = new ymaps.Placemark([${place.latitude}, ${place.longitude}],
                {
                    hintContent: 'latitude: ${place.latitude}, longitude: ${place.longitude}',
                    iconContent: '7',
                    balloonContent: '${nameVok}'
                }, {
                    preset: 'islands#governmentCircleIcon',
                    iconColor: '${colors}'
                });
            clusterer1.add(myPlacemark);
            </c:forEach>
            myMap.geoObjects.add(clusterer1);
            clusterer1.clear();
        }

        function placesPositiveness() {
            clearMap();
            var myPlacemark;
            <c:forEach items="${pl1}" var="place">
            myPlacemark = new ymaps.Placemark([${place.latitude}, ${place.longitude}],
                {
                    hintContent: 'latitude: ${place.latitude}, longitude: ${place.longitude}',
                    iconContent: '7',
                    balloonContent: '${nameVok}'
                }, {
                    preset: 'islands#governmentCircleIcon',
                    iconColor: '${colors}'
                });
            clusterer2.add(myPlacemark);
            </c:forEach>
            myMap.geoObjects.add(clusterer2);
            clusterer2.clear();
        }

        function placesNegativity() {
            clearMap();
            var myPlacemark;
            <c:forEach items="${pl2}" var="place">
            myPlacemark = new ymaps.Placemark([${place.latitude}, ${place.longitude}],
                {
                    hintContent: 'latitude: ${place.latitude}, longitude: ${place.longitude}',
                    iconContent: '7',
                    balloonContent: '${nameVok}'
                }, {
                    preset: 'islands#governmentCircleIcon',
                    iconColor: '${colors}'
                });
            clusterer3.add(myPlacemark);
            </c:forEach>
            myMap.geoObjects.add(clusterer3);
            clusterer3.clear();
        }

        function placesRecreation() {
            clearMap();
            var myPlacemark;
            <c:forEach items="${pl3}" var="place">
            myPlacemark = new ymaps.Placemark([${place.latitude}, ${place.longitude}],
                {
                    hintContent: 'latitude: ${place.latitude}, longitude: ${place.longitude}',
                    iconContent: '7',
                    balloonContent: '${nameVok}'
                }, {
                    preset: 'islands#governmentCircleIcon',
                    iconColor: '${colors}'
                });
            clusterer4.add(myPlacemark);
            </c:forEach>
            myMap.geoObjects.add(clusterer4);
            clusterer4.clear();
        }

        function placesCulture() {
            clearMap();
            var myPlacemark;
            <c:forEach items="${pl4}" var="place">
            myPlacemark = new ymaps.Placemark([${place.latitude}, ${place.longitude}],
                {
                    hintContent: 'latitude: ${place.latitude}, longitude: ${place.longitude}',
                    iconContent: '7',
                    balloonContent: '${nameVok}'
                }, {
                    preset: 'islands#governmentCircleIcon',
                    iconColor: '${colors}'
                });
            clusterer5.add(myPlacemark);
            </c:forEach>
            myMap.geoObjects.add(clusterer5);
            clusterer5.clear();
        }

        function placesFood() {
            clearMap();
            var myPlacemark;
            <c:forEach items="${pl5}" var="place">
            myPlacemark = new ymaps.Placemark([${place.latitude}, ${place.longitude}],
                {
                    hintContent: 'latitude: ${place.latitude}, longitude: ${place.longitude}',
                    iconContent: '7',
                    balloonContent: '${nameVok}'
                }, {
                    preset: 'islands#governmentCircleIcon',
                    iconColor: '${colors}'
                });
            clusterer6.add(myPlacemark);

            </c:forEach>
            myMap.geoObjects.add(clusterer6);
            clusterer6.clear();
        }


        function popularPlace() {
            <c:forEach items="${hexagons}" var="point">
            polygon = new ymaps.Polygon([
                // Указываем координаты вершин многоугольника.
                // Координаты вершин внешнего контура.
                [<c:forEach items="${point}" var="element">
                    [${element.latitude}, ${element.longitude}],
                    </c:forEach>
                ]
            ], {
                // Описываем свойства геообъекта.
                // Содержимое балуна.
                hintContent: "Многоугольник"
            }, {
                // Задаем опции геообъекта.
                // Цвет заливки.
                fillColor: '#dd8e8810',
                // Ширина обводки.
                strokeWidth: 0
            });

            myMap.geoObjects.add(polygon);
            </c:forEach>
        }

        function clearMap() {
            myMap.geoObjects.removeAll();
        }

    </script>

    <script src="https://vk.com/js/api/openapi.js?150" type="text/javascript"></script>
</head>

<body>
<nav>
    <div class="block_inset">

        <div class="inset_num" id="inset_num1">
            <div class="inset_content">
                <h2 STYLE="text-align: center;"><br/>Hello, friend!<br/></h2>
                <img src="/resources/css/hello.png" class="round" style="margin-top: 20px">
                <h3 STYLE="text-align: left;"><br/>LookForing is a system for people who want to find interesting
                    places.
                    It helps you to choose popular or nearest objects that are worth your attention.<p></p>
                    In order to start you just need to log in with your «VKontakte» account.<p></p>
                    Hope this application will be useful for you.<br/></h3>
            </div>
        </div>

        <div class="reltr" id="inse_num" onClick="click_header_inset(1);"></div>

    </div>

    <div class="layer1">
        <h1>L<img src="/resources/css/Logo.png" style="width: 45px; height: 27px;">kForing</h1>
    </div>
    <div class="layer2">
        <form id="form" action="https://oauth.vk.com/authorize/">
            <ul class="menu">
                <li onclick="form.submit();">Log in with VK
                    <input type="hidden" name="client_id" value="6246163"/>
                    <input type="hidden" name="display" value="popup"/>
                    <input type="hidden" name="redirect_uri" value="http://localhost/account"/>
                    <input type="hidden" name="response_type" value="code"/>
                </li>
                <li>Subjects
                    <ul class="submenu">
                        <li onclick="placesSport()">Sport</li>
                        <li onclick="placesPositiveness()">Positiveness</li>
                        <li onclick="placesNegativity()">Negativity</li>
                        <li onclick="placesRecreation()">Recreation</li>
                        <li onclick="placesCulture()">Culture</li>
                        <li onclick="placesFood()">Food</li>
                    </ul>
                </li>
                <li onclick="popularPlace()">Popular</li>
                <li onclick="initgeo()">Nearest</li>
                <li onclick="clearMap()">Clear</li>


            </ul>
        </form>
    </div>
    <div align="center" class="clear">
        <div id="map" style="z-index:0; width: 97%; height: 85%; margin-top: 30%"></div>
    </div>
</nav>
</body>
</html>