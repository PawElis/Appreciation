<%--
  Created by IntelliJ IDEA.
  User: Павел
  Date: 29.10.2017
  Time: 16:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Hello user</title>
    <meta charset="UTF-8">
    <script type="text/javascript" src="resources/js/index.js"></script>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/css/icon.png" type="image/x-icon"/>
    <link rel="stylesheet" href="/resources/css/index_style.css">
    <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
    <script type="text/javascript">
        ymaps.ready(init);
        var myMap,
            myPlacemark,
            polygon;

        function init() {
            myMap = new ymaps.Map("map", {
                center: [59.9868, 30.1584],
                zoom: 8,
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

            myMap.options.set('maxZoom', 14);
            myMap.options.set('minZoom', 8);
            /*
             getPointData = function (index) {
             return {
             balloonContentHeader: '<span style="font-size: small; "><b><a target="_blank" href="https://yandex.ru">Здесь может быть ваша ссылка</a></b></span>',
             balloonContentBody: '<p>Ваше имя: <input name="login"></p><p>Телефон в формате 2xxx-xxx:  <input></p><p><input type="submit" value="Отправить"></p>',
             balloonContentFooter: '<span style="font-size: xx-small; ">Информация предоставлена: </span> балуном <strong>метки ' + index + '</strong>',
             clusterCaption: 'метка <strong>' + index + '</strong>'
             };
             };
             getPointOptions = function () {
             return {
             preset: 'islands#violetIcon'
             };
             };
             */

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
        }
        function popularPlace() {
            <c:forEach items="${hexagons}" var="point">
            polygon = new ymaps.Polygon([
                // Указываем координаты вершин многоугольника.
                // Координаты вершин внешнего контура.
                [ <c:forEach items="${point}" var="element">
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

        function Advised() {
            clearMap();
            var myPlacemark;
            /*pl6*/
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
            clusterer7.add(myPlacemark);

            </c:forEach>
            myMap.geoObjects.add(clusterer7);
            clusterer7.clear();
        }

        function clearMap() {
            myMap.geoObjects.removeAll();
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

    </script>

    <script src="https://vk.com/js/api/openapi.js?150" type="text/javascript"></script>
</head>
<body>
<nav>
    <div class="block_inset">

        <div class="inset_num" id="inset_num1">
            <div class="inset_content">
                <h2 STYLE="text-align: center;"><br />Hello, <br /> ${name}!<br /></h2>
                <img src="${photo}" class="round">
                <h3 STYLE="text-align: left;"><br/>LookForing is a system for people who want to find interesting
                    places.
                    It helps you to choose popular or nearest objects that are worth your attention.<p></p>
                    Hope this application will be useful for you.<br/></h3>
            </div>
        </div>

        <div class="reltr" id="inse_num" onClick="click_header_inset(1);"></div>

    </div>

    <div class="layer1">
        <h1>L<img src="/resources/css/Logo.png"style="width: 45px; height: 27px;">kForing</h1>
    </div>
    <div class="layer2">
        <form>
            <ul class="menu">
                <li onclick="Advised()">Advised</li>
                <li onclick="popularPlace()">Popular</li>
                <li onclick="initgeo()">Nearest</li>
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
                <li onclick="clearMap()">Clear</li>
                <li onclick="location.href = '${pageContext.request.contextPath}/';">Log out
            </li>
            </ul>
        </form>
    </div>
    <div align="center" class="clear">
        <div id="map" style="width: 97%; height: 85%; margin-top: 30%"></div>
    </div>
</nav>
</body>
</html>