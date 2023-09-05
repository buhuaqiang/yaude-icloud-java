function fnW(str) {
    var num;
    str >= 10 ? num = str : num = "0" + str;
    return num;
}
//獲取當前時間
var timer = setInterval(function () {
    var date = new Date();
    var year = date.getFullYear(); //當前年份
    var month = date.getMonth(); //當前月份
    var data = date.getDate(); //天
    var hours = date.getHours(); //小時
    var minute = date.getMinutes(); //分
    var second = date.getSeconds(); //秒
    var day = date.getDay(); //獲取當前星期幾 
    var ampm = hours < 12 ? 'am' : 'pm';
    $('#time').html(fnW(hours) + ":" + fnW(minute) + ":" + fnW(second));
    $('#date').html('<span>' + year + '/' + (month + 1) + '/' + data + '</span><span>' + ampm + '</span><span>周' + day + '</span>')

}, 1000)



//頁面地圖數據
var geoCoordMap = {
    '海門': [121.15, 31.89],
    '鄂爾多斯': [109.781327, 39.608266],
    '招遠': [120.38, 37.35],
    '舟山': [122.207216, 29.985295],
    '齊齊哈爾': [123.97, 47.33],
    '鹽城': [120.13, 33.38],
    '赤峰': [118.87, 42.28],
    '青島': [120.33, 36.07],
    '乳山': [121.52, 36.89],
    '金昌': [102.188043, 38.520089],
    '泉州': [118.58, 24.93],
    '萊西': [120.53, 36.86],
    '日照': [119.46, 35.42],
    '膠南': [119.97, 35.88],
    '南通': [121.05, 32.08],
    '拉薩': [91.11, 29.97],
    '云浮': [112.02, 22.93],
    '梅州': [116.1, 24.55],
    '文登': [122.05, 37.2],
    '上海': [121.48, 31.22],
    '攀枝花': [101.718637, 26.582347],
    '威海': [122.1, 37.5],
    '承德': [117.93, 40.97],
    '廈門': [118.1, 24.46],
    '汕尾': [115.375279, 22.786211],
    '潮州': [116.63, 23.68],
    '丹東': [124.37, 40.13],
    '太倉': [121.1, 31.45],
    '曲靖': [103.79, 25.51],
    '煙臺': [121.39, 37.52],
    '福州': [119.3, 26.08],
    '瓦房店': [121.979603, 39.627114],
    '即墨': [120.45, 36.38],
    '撫順': [123.97, 41.97],
    '玉溪': [102.52, 24.35],
    '張家口': [114.87, 40.82],
    '陽泉': [113.57, 37.85],
    '萊州': [119.942327, 37.177017],
    '湖州': [120.1, 30.86],
    '汕頭': [116.69, 23.39],
    '昆山': [120.95, 31.39],
    '寧波': [121.56, 29.86],
    '湛江': [110.359377, 21.270708],
    '揭陽': [116.35, 23.55],
    '榮成': [122.41, 37.16],
    '連云港': [119.16, 34.59],
    '葫蘆島': [120.836932, 40.711052],
    '常熟': [120.74, 31.64],
    '東莞': [113.75, 23.04],
    '河源': [114.68, 23.73],
    '淮安': [119.15, 33.5],
    '泰州': [119.9, 32.49],
    '南寧': [108.33, 22.84],
    '營口': [122.18, 40.65],
    '惠州': [114.4, 23.09],
    '江陰': [120.26, 31.91],
    '蓬萊': [120.75, 37.8],
    '韶關': [113.62, 24.84],
    '嘉峪關': [98.289152, 39.77313],
    '廣州': [113.23, 23.16],
    '延安': [109.47, 36.6],
    '太原': [112.53, 37.87],
    '清遠': [113.01, 23.7],
    '中山': [113.38, 22.52],
    '昆明': [102.73, 25.04],
    '壽光': [118.73, 36.86],
    '盤錦': [122.070714, 41.119997],
    '長治': [113.08, 36.18],
    '深圳': [114.07, 22.62],
    '珠海': [113.52, 22.3],
    '宿遷': [118.3, 33.96],
    '咸陽': [108.72, 34.36],
    '銅川': [109.11, 35.09],
    '平度': [119.97, 36.77],
    '佛山': [113.11, 23.05],
    '海口': [110.35, 20.02],
    '江門': [113.06, 22.61],
    '章丘': [117.53, 36.72],
    '肇慶': [112.44, 23.05],
    '大連': [121.62, 38.92],
    '臨汾': [111.5, 36.08],
    '吳江': [120.63, 31.16],
    '石嘴山': [106.39, 39.04],
    '沈陽': [123.38, 41.8],
    '蘇州': [120.62, 31.32],
    '茂名': [110.88, 21.68],
    '嘉興': [120.76, 30.77],
    '長春': [125.35, 43.88],
    '膠州': [120.03336, 36.264622],
    '銀川': [106.27, 38.47],
    '張家港': [120.555821, 31.875428],
    '三門峽': [111.19, 34.76],
    '錦州': [121.15, 41.13],
    '南昌': [115.89, 28.68],
    '柳州': [109.4, 24.33],
    '三亞': [109.511909, 18.252847],
    '自貢': [104.778442, 29.33903],
    '吉林': [126.57, 43.87],
    '陽江': [111.95, 21.85],
    '瀘州': [105.39, 28.91],
    '西寧': [101.74, 36.56],
    '宜賓': [104.56, 29.77],
    '呼和浩特': [111.65, 40.82],
    '成都': [104.06, 30.67],
    '大同': [113.3, 40.12],
    '鎮江': [119.44, 32.2],
    '桂林': [110.28, 25.29],
    '張家界': [110.479191, 29.117096],
    '宜興': [119.82, 31.36],
    '北海': [109.12, 21.49],
    '西安': [108.95, 34.27],
    '金壇': [119.56, 31.74],
    '東營': [118.49, 37.46],
    '牡丹江': [129.58, 44.6],
    '遵義': [106.9, 27.7],
    '紹興': [120.58, 30.01],
    '揚州': [119.42, 32.39],
    '常州': [119.95, 31.79],
    '濰坊': [119.1, 36.62],
    '重慶': [106.54, 29.59],
    '臺州': [121.420757, 28.656386],
    '南京': [118.78, 32.04],
    '濱州': [118.03, 37.36],
    '貴陽': [106.71, 26.57],
    '無錫': [120.29, 31.59],
    '本溪': [123.73, 41.3],
    '克拉瑪依': [84.77, 45.59],
    '渭南': [109.5, 34.52],
    '馬鞍山': [118.48, 31.56],
    '寶雞': [107.15, 34.38],
    '焦作': [113.21, 35.24],
    '句容': [119.16, 31.95],
    '北京': [116.46, 39.92],
    '徐州': [117.2, 34.26],
    '衡水': [115.72, 37.72],
    '包頭': [110, 40.58],
    '綿陽': [104.73, 31.48],
    '烏魯木齊': [87.68, 43.77],
    '棗莊': [117.57, 34.86],
    '杭州': [120.19, 30.26],
    '淄博': [118.05, 36.78],
    '鞍山': [122.85, 41.12],
    '溧陽': [119.48, 31.43],
    '庫爾勒': [86.06, 41.68],
    '安陽': [114.35, 36.1],
    '開封': [114.35, 34.79],
    '濟南': [117, 36.65],
    '德陽': [104.37, 31.13],
    '溫州': [120.65, 28.01],
    '九江': [115.97, 29.71],
    '邯鄲': [114.47, 36.6],
    '臨安': [119.72, 30.23],
    '蘭州': [103.73, 36.03],
    '滄州': [116.83, 38.33],
    '臨沂': [118.35, 35.05],
    '南充': [106.110698, 30.837793],
    '天津': [117.2, 39.13],
    '富陽': [119.95, 30.07],
    '泰安': [117.13, 36.18],
    '諸暨': [120.23, 29.71],
    '鄭州': [113.65, 34.76],
    '哈爾濱': [126.63, 45.75],
    '聊城': [115.97, 36.45],
    '蕪湖': [118.38, 31.33],
    '唐山': [118.02, 39.63],
    '平頂山': [113.29, 33.75],
    '邢臺': [114.48, 37.05],
    '德州': [116.29, 37.45],
    '濟寧': [116.59, 35.38],
    '荊州': [112.239741, 30.335165],
    '宜昌': [111.3, 30.7],
    '義烏': [120.06, 29.32],
    '麗水': [119.92, 28.45],
    '洛陽': [112.44, 34.7],
    '秦皇島': [119.57, 39.95],
    '株洲': [113.16, 27.83],
    '石家莊': [114.48, 38.03],
    '萊蕪': [117.67, 36.19],
    '常德': [111.69, 29.05],
    '保定': [115.48, 38.85],
    '湘潭': [112.91, 27.87],
    '金華': [119.64, 29.12],
    '岳陽': [113.09, 29.37],
    '長沙': [113, 28.21],
    '衢州': [118.88, 28.97],
    '廊坊': [116.7, 39.53],
    '菏澤': [115.480656, 35.23375],
    '合肥': [117.27, 31.86],
    '武漢': [114.31, 30.52],
    '大慶': [125.03, 46.58],
    '安徽省': [117.17, 31.52],
    '北京市': [116.24, 39.55],
    '重慶市': [106.54, 29.59],
    '福建省': [119.18, 26.05],
    '甘肅省': [103.51, 36.04],
    '廣東省': [113.14, 23.08],
    '廣西壯族自治區': [108.19, 22.48],
    '貴州省': [106.42, 26.35],
    '海南省': [110.20, 20.02],
    '河北省': [114.30, 38.02],
    '河南省': [113.40, 34.46],
    '黑龍江省': [128.36, 45.44],
    '湖北省': [112.27, 30.15],
    '湖南省': [112.59, 28.12],
    '吉林省': [125.19, 43.54],
    '江蘇省': [118.46, 32.03],
    '江西省': [115.55, 28.40],
    '遼寧省': [123.25, 41.48],
    '內蒙古': [108.41, 40.48],
    '內蒙古自治區': [108.41, 40.48],
    '寧夏回族自治區': [106.16, 38.27],
    '青海省': [101.48, 36.38],
    '山東省': [118.00, 36.40],
    '山西省': [112.33, 37.54],
    '陜西省': [108.57, 34.17],
    '上海市': [121.29, 31.14],
    '海南': [108.77, 19.10],
    '四川省': [104.04, 30.40],
    '天津市': [117.12, 39.02],
    '西藏自治區': [91.08, 29.39],
    '新疆維吾爾自治區': [87.36, 43.45],
    '云南省': [102.42, 25.04],
    '浙江省': [120.10, 30.16],
    '澳門特別行政區': [115.07, 21.33],
    '臺灣省': [121.21, 23.53],
    '香港特別行政區': [114.1, 22.2]
};

$('.select').on('blur', function () {
        $(this).find('.select-ul').hide();
    })
    //下拉框點擊出現下拉框內容
$('.select-div').on('click', function () {
    if ($(this).siblings('.select-ul').is(":hidden")) {
        $(this).siblings('.select-ul').show();
    } else {
        $(this).siblings('.select-ul').hide();
    }
})


$('.select-ul').on('click', 'li', function () {
    $(this).addClass('active').siblings('li').removeClass('active').parent().hide().siblings('.select-div').html($(this).html());
    var parentDiv = $(this).parent().parent().parent();
})

//鼠標滑動到按鈕，按鈕內容變成白色
var imgName;
$('.title-box').children('button').hover(function () {
    imgName = $(this).children('img').attr('src').split('.png')[0];
    $(this).children('img').attr('src', imgName + '_on.png');
}, function () {
    $(this).children('img').attr('src', imgName + '.png');

});


var startColor = ['#0e94eb', '#c440ef', '#efb013', '#2fda07', '#d8ef13', '#2e4af8', '#0eebc4', '#f129b1', '#17defc', '#f86363'];
var borderStartColor = ['#0077c5', '#a819d7', '#c99002', '#24bc00', '#b6cb04', '#112ee2', '#00bd9c', '#ce078f', '#00b2cd', '#ec3c3c'];



//入庫量占比，帶邊框效果的餅圖
function chart1() {
    //data 為模擬數據
    var data = [{
        name: '順豐',
        value: 192581,
        percent: '30.8721',
    }, {
        name: '京東',
        value: 215635,
        percent: '34.076',
    }, {
        name: 'EMS',
        value: 224585,
        percent: '35.49',
    }];
    var myChart = echarts.init(document.getElementById('pie'));
    var myChart1 = echarts.init(document.getElementById('pie1'));
    window.addEventListener('resize', function () {
        myChart.resize();
        myChart1.resize();
    });

    var str = '';
    for (var i = 0; i < data.length; i++) {
        str += '<p><span><i class="legend" style="background:' + startColor[i] + '"></i></span>' + data[i].name + '<span class="pie-number" style="color:' + startColor[i] + '">' + data[i].value + '</span>' + Number(data[i].percent).toFixed(2) + '%</p>';
    }

    $('.pie-data').append(str);


    function deepCopy(obj) {
        if (typeof obj !== 'object') {
            return obj;
        }
        var newobj = {};
        for (var attr in obj) {
            newobj[attr] = obj[attr];
        }
        return newobj;
    }
    var xData = [],
        yData = [];
    data.map((a, b) => {
        xData.push(a.name);
        yData.push(a.value);
    });


    var RealData = [];
    var borderData = [];
    data.map((item, index) => {
        var newobj = deepCopy(item);
        var newobj1 = deepCopy(item);
        RealData.push(newobj);
        borderData.push(newobj1);
    });
    RealData.map((item, index) => {
        item.itemStyle = {
            normal: {
                color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 1,
                    colorStops: [{
                        offset: 0,
                        color: startColor[index] // 0% 處的顏色
                }, {
                        offset: 1,
                        color: startColor[index] // 100% 處的顏色
                }],
                    globalCoord: false // 缺省為 false
                },
            }
        }
    });
    borderData.map((item, index) => {
        item.itemStyle = {
            normal: {
                color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 1,
                    colorStops: [{
                        offset: 0,
                        color: borderStartColor[index] // 0% 處的顏色
                }, {
                        offset: 1,
                        color: borderStartColor[index] // 100% 處的顏色
                }],
                    globalCoord: false // 缺省為 false
                },
            }
        }
    });
    var option = {
        tooltip: {
            trigger: 'item',
            //            position: ['30%', '50%'],
            confine: true,
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        series: [
        // 主要展示層的
            {
                radius: ['50%', '85%'],
                center: ['50%', '50%'],
                type: 'pie',
                label: {
                    normal: {
                        show: false
                    },
                    emphasis: {
                        show: false
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    },
                    emphasis: {
                        show: false
                    }
                },
                name: "派件入庫量占比內容",
                data: RealData
        },
        // 邊框的設置
            {
                radius: ['45%', '50%'],
                center: ['50%', '50%'],
                type: 'pie',
                label: {
                    normal: {
                        show: false
                    },
                    emphasis: {
                        show: false
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    },
                    emphasis: {
                        show: false
                    }
                },
                animation: false,
                tooltip: {
                    show: false
                },
                data: borderData
        }
    ]
    };

    myChart.setOption(option);
    myChart1.setOption(option);
}

chart1()

//----------------------派件入庫量占比內容end---------------

//------------廣東省寄派件數據內容---------------
//點擊篩選按鈕
$('#filBtn').on('click', function () {
        if ($('#filCon').is(":hidden")) {
            $('#filCon').attr('style', 'display:flex');
        } else {
            $('#filCon').hide();
        }
    })
    //點擊篩選按鈕end


function chart2(chartType) {
    var data = [
        {
            name: '廣州市',
            value: 120057.34
            },
        {
            name: '韶關市',
            value: 15477.48
            },
        {
            name: '深圳市',
            value: 131686.1
            },
        {
            name: '珠海市',
            value: 6992.6
            },
        {
            name: '汕頭市',
            value: 44045.49
            },
        {
            name: '佛山市',
            value: 40689.64
            },
        {
            name: '江門市',
            value: 37659.78
            },
        {
            name: '湛江市',
            value: 45180.97
            },
        {
            name: '茂名市',
            value: 5204.26
            },
        {
            name: '肇慶市',
            value: 21900.9
            },
        {
            name: '惠州市',
            value: 4918.26
            },
        {
            name: '梅州市',
            value: 5881.84
            },
        {
            name: '汕尾市',
            value: 4178.01
            },
        {
            name: '河源市',
            value: 2227.92
            },
        {
            name: '陽江市',
            value: 2180.98
            },
        {
            name: '清遠市',
            value: 9172.94
            },
        {
            name: '東莞市',
            value: 3368
            },
        {
            name: '中山市',
            value: 306.98
            },
        {
            name: '潮州市',
            value: 810.66
            },
        {
            name: '揭陽市',
            value: 542.2
            },
        {
            name: '云浮市',
            value: 256.38
            }]

    var myChart = echarts.init(document.getElementById('gdMap'));
    var myCharts = echarts.init(document.getElementById('gdMaps'));
    window.addEventListener('resize', function () {
        myChart.resize();
        myCharts.resize();
    });
    var yMax = 0;
    for (var j = 0; j < data.length; j++) {
        if (yMax < data[j].value) {
            yMax = data[j].value;
        }
    }
        myChart.hideLoading();
        myCharts.hideLoading();
        var option = {
            animation: true,
            tooltip: {
                show: true
            },
            visualMap: {
                min: 0,
                max: yMax,
                text: ['高', '低'],
                orient: 'horizontal',
                itemWidth: 15,
                itemHeight: 200,
                right: 0,
                bottom: 30,
                inRange: {
                    color: ['#75ddff', '#0e94eb']
                },
                textStyle: {
                    color: 'white'
                }
            },
            series: [
                {
                    name: '數據名稱',
                    type: 'map',
                    mapType: '廣東',
                    selectedMode: 'multiple',
                    tooltip: {
                        trigger: 'item',
                        formatter: '{b}<br/>{c} (件)'
                    },
                    itemStyle: {
                        normal: {
                            borderWidth: 1,
                            borderColor: '#0e94eb',
                            label: {
                                show: false
                            }
                        },
                        emphasis: { // 也是選中樣式
                            borderWidth: 1,
                            borderColor: '#fff',
                            backgroundColor: 'red',
                            label: {
                                show: true,
                                textStyle: {
                                    color: '#fff'
                                }
                            }
                        }
                    },
                    data: data,
            }
            ]
        };

        myChart.setOption(option);
        myCharts.setOption(option);
}
chart2('');

//------------廣東省寄派件數據內容end---------------

//cityName全國的省級行政區域數據
var cityName = [{
    "ProID": 1,
    "name": "北京",
    "ProSort": 1,
    "firstP": "B",
    "ProRemark": "直轄市"
}, {
    "ProID": 2,
    "name": "天津",
    "ProSort": 2,
    "firstP": "T",
    "ProRemark": "直轄市"
}, {
    "ProID": 3,
    "name": "河北",
    "ProSort": 5,
    "firstP": "H",
    "ProRemark": "省份"
}, {
    "ProID": 4,
    "name": "山西",
    "ProSort": 6,
    "firstP": "S",
    "ProRemark": "省份"
}, {
    "ProID": 5,
    "name": "內蒙古",
    "ProSort": 32,
    "firstP": "N",
    "ProRemark": "自治區"
}, {
    "ProID": 6,
    "name": "遼寧",
    "ProSort": 8,
    "firstP": "L",
    "ProRemark": "省份"
}, {
    "ProID": 7,
    "name": "吉林",
    "ProSort": 9,
    "firstP": "J",
    "ProRemark": "省份"
}, {
    "ProID": 8,
    "name": "黑龍江",
    "ProSort": 10,
    "firstP": "H",
    "ProRemark": "省份"
}, {
    "ProID": 9,
    "name": "上海",
    "ProSort": 3,
    "firstP": "S",
    "ProRemark": "直轄市"
}, {
    "ProID": 10,
    "name": "江蘇",
    "ProSort": 11,
    "firstP": "J",
    "ProRemark": "省份"
}, {
    "ProID": 11,
    "name": "浙江",
    "ProSort": 12,
    "firstP": "Z",
    "ProRemark": "省份"
}, {
    "ProID": 12,
    "name": "安徽",
    "ProSort": 13,
    "firstP": "A",
    "ProRemark": "省份"
}, {
    "ProID": 13,
    "name": "福建",
    "ProSort": 14,
    "firstP": "F",
    "ProRemark": "省份"
}, {
    "ProID": 14,
    "name": "江西",
    "ProSort": 15,
    "firstP": "J",
    "ProRemark": "省份"
}, {
    "ProID": 15,
    "name": "山東",
    "ProSort": 16,
    "firstP": "S",
    "ProRemark": "省份"
}, {
    "ProID": 16,
    "name": "河南",
    "ProSort": 17,
    "firstP": "H",
    "ProRemark": "省份"
}, {
    "ProID": 17,
    "name": "湖北",
    "ProSort": 18,
    "firstP": "H",
    "ProRemark": "省份"
}, {
    "ProID": 18,
    "name": "湖南",
    "ProSort": 19,
    "firstP": "H",
    "ProRemark": "省份"
}, {
    "ProID": 19,
    "name": "廣東",
    "ProSort": 20,
    "firstP": "G",
    "ProRemark": "省份"
}, {
    "ProID": 20,
    "name": "海南",
    "ProSort": 24,
    "firstP": "H",
    "ProRemark": "省份"
}, {
    "ProID": 21,
    "name": "廣西",
    "ProSort": 28,
    "firstP": "G",
    "ProRemark": "自治區"
}, {
    "ProID": 22,
    "name": "甘肅",
    "ProSort": 21,
    "firstP": "G",
    "ProRemark": "省份"
}, {
    "ProID": 23,
    "name": "陜西省",
    "ProSort": 27,
    "firstP": "S",
    "ProRemark": "省份"
}, {
    "ProID": 24,
    "name": "新疆維吾爾",
    "ProSort": 31,
    "firstP": "X",
    "ProRemark": "自治區"
}, {
    "ProID": 25,
    "name": "青海",
    "ProSort": 26,
    "firstP": "Q",
    "ProRemark": "省份"
}, {
    "ProID": 26,
    "name": "寧夏",
    "ProSort": 30,
    "firstP": "N",
    "ProRemark": "自治區"
}, {
    "ProID": 27,
    "name": "重慶",
    "ProSort": 4,
    "firstP": "C",
    "ProRemark": "直轄市"
}, {
    "ProID": 28,
    "name": "四川省",
    "ProSort": 22,
    "firstP": "S",
    "ProRemark": "省份"
}, {
    "ProID": 29,
    "name": "貴州省",
    "ProSort": 23,
    "firstP": "G",
    "ProRemark": "省份"
}, {
    "ProID": 30,
    "name": "云南省",
    "ProSort": 25,
    "firstP": "Y",
    "ProRemark": "省份"
}, {
    "ProID": 31,
    "name": "西藏",
    "ProSort": 29,
    "firstP": "X",
    "ProRemark": "自治區"
}, {
    "ProID": 32,
    "name": "臺灣",
    "ProSort": 7,
    "firstP": "T",
    "ProRemark": "省份"
}, {
    "ProID": 33,
    "name": "澳門",
    "ProSort": 33,
    "firstP": "A",
    "ProRemark": "特別行政區"
}, {
    "ProID": 34,
    "name": "香港",
    "ProSort": 34,
    "firstP": "X",
    "ProRemark": "特別行政區"
}]

addCityBtn(cityName);

function addCityBtn(data) {
    var li_con = '';
    for (var i = 0; i < data.length; i++) {
        li_con += '<li>' + data[i].name + '</li>'
    }
    $('#city').html(li_con);
    $('#citys').html(li_con);
}

$('.city-btn').on('click', 'li', function () {
    var str;
    var patt = [/[a-z]/i, /[a-e]/i, /[f-i]/i, /[k-o]/i, /[p-t]/i, /[u-z]/i];
    var index = $(this).index();
    var li_con = '';
    for (var i = 0; i < cityName.length; i++) {
        str = cityName[i].firstP;
        if (patt[index].test(str)) {
            li_con += '<li>' + cityName[i].name + '</li>'
        }
    }

    $(this).addClass('active').siblings('li').removeClass('active');
    if (index == 0) {
        $('#city').children().removeClass('active');
        if ($(this).parent().data('city') == 1) {
            $('.ranking-box').show();
            if ($("#barType").find('.active').data('value') == 1) {
                $('#titleQ').html('<span>全網</span>到珠海');
            } else if ($("#barType").find('.active').data('value') == 2) {
                $('#titleQ').html('珠海到<span>全網</span>')
            }
            $('#city').html(li_con);
        } else if ($(this).parent().data('city') == 2) {
            if ($('.cont-div').eq(0).css('visibility') != 'hidden') {
                $('.ranking-box').show();
            }
            if ($("#barTypes").find('.active').data('value') == 1) {
                $('#titleQs').html('<span>全網</span>到珠海');
            } else if ($("#barTypes").find('.active').data('value') == 2) {
                $('#titleQs').html('珠海到<span>全網</span>')
            }
            $('#citys').html(li_con);
        }
    } else {
        if ($(this).parent().data('city') == 1) {
            $('#city').html(li_con);
        } else if ($(this).parent().data('city') == 2) {
            $('#citys').html(li_con);
        }
    }


})


$('#city').on('click', 'li', function () {
    $(this).addClass('active').siblings('li').removeClass('active');
    $('.center-bottom .ranking-box').hide();
    if ($("#barType").find('.active').data('value') == 1) {
        $('#titleQ').html('<span>' + $(this).html() + '</span>到珠海');
    } else if ($("#barType").find('.active').data('value') == 2) {
        $('#titleQ').html('珠海到<span>' + $(this).html() + '</span>')
    }
})

$('#citys').on('click', 'li', function () {
    $(this).addClass('active').siblings('li').removeClass('active');
    $('.pop-data .ranking-box').hide();
    if ($("#barTypes").find('.active').data('value') == 1) {
        $('#titleQs').html('<span>' + $(this).html() + '</span>到珠海');
    } else if ($("#barTypes").find('.active').data('value') == 2) {
        $('#titleQs').html('珠海到<span>' + $(this).html() + '</span>')
    }
})

//寄派件選擇
$("#barType").on('click', 'li', function () {
    $(this).addClass('active').siblings('li').removeClass('active');
    $('#barTitle').html($(this).html() + '數據');
    $('#tabBtn').data('state', $(this).data('value'));
    if ($(this).data('value') == 1) {
        $('.table1').eq(0).show().siblings('table').hide();
    } else if ($(this).data('value') == 2) {
        $('.table1').eq(1).show().siblings('table').hide();
    }
    chart3($(this).data('value'), 0);
    chart4(chart4Data, $(this).data('value'), 0);
})

//寄派件選擇
$("#barTypes").on('click', 'li', function () {
    $(this).addClass('active').siblings('li').removeClass('active');
    $('#barTitles').html($(this).html() + '數據');
    $('#tabBtns').data('state', $(this).data('value'));
    if ($(this).data('value') == 1) {
        $('.table2').eq(0).show().siblings('table').hide();
    } else if ($(this).data('value') == 2) {
        $('.table2').eq(1).show().siblings('table').hide();
    }
    chart3($(this).data('value'), 1);
    chart4(chart4Data, $(this).data('value'), 1);

})


function chart3(type, chartType) {
    var myChart = echarts.init(document.getElementById('chart3'));
    var myCharts = echarts.init(document.getElementById('chart3s'));
    window.addEventListener('resize', function () {
        myChart.resize();
        myCharts.resize();
    });

    //    設置背景陰影的參數，獲取數據的最大值

    var data; //橫坐標數據，不動
    var data_; //模擬數據
    if (type == 1) {
        data_ = [{
                name: "入庫件",
                value: 584
            },
            {
                name: "滯留件",
                value: 152
            }, {
                name: "丟失件",
                value: 100
            },
            {
                name: "正常件",
                value: 689
            },
            {
                name: "派送件",
                value: 200
            }, {
                name: "自提件",
                value: 121
            }, {
                name: "退簽件",
                value: 92
            }]
    } else if (type == 2) {
        data_ = [{
                name: "入庫件",
                value: 568
                }, {
                name: "丟失件",
                value: 287
                }, {
                name: "滯留件",
                value: 120
                },
            {
                name: "撤銷件",
                value: 152
                },
            {
                name: "出庫件",
                value: 125
                }, {
                name: "正常件",
                value: 122
        }]
    }
    var series_data; //繪制圖表的數據
    //繪制圖表
    var yMax = 0;
    for (var j = 0; j < data_.length; j++) {
        if (yMax < data_[j].value) {
            yMax = data_[j].value;
        }
    }
    var dataShadow = [];
    for (var i = 0; i < 10; i++) {
        dataShadow.push(yMax * 2);
    }

    if (type == 1) {
        data = ['入庫件', '在庫件', '出庫件', '退簽件', '丟失件'];

        if (chartType == '') {
            $(' .dph-data1').html(data_[0].value);
            $(' .dph-data2').html(data_[1].value + data_[3].value);
            $(' .dph-data3').html(data_[3].value);
            $(' .dph-data4').html(data_[2].value);
            $(' .dph-data5').html(data_[1].value);
            $(' .dph-data6').html(data_[4].value + data_[5].value);
            $(' .dph-data7').html(data_[4].value);
            $(' .dph-data8').html(data_[5].value);
            $(' .dph-data9').html(data_[6].value);
        } else if (chartType == 0) {
            $('.table1 .dph-data1').html(data_[0].value);
            $('.table1 .dph-data2').html(data_[1].value + data_[3].value);
            $('.table1 .dph-data3').html(data_[3].value);
            $('.table1 .dph-data4').html(data_[2].value);
            $('.table1 .dph-data5').html(data_[1].value);
            $('.table1 .dph-data6').html(data_[4].value + data_[5].value);
            $('.table1 .dph-data7').html(data_[4].value);
            $('.table1 .dph-data8').html(data_[5].value);
            $('.table1 .dph-data9').html(data_[6].value);
        } else if (chartType == 1) {
            $('.table2 .dph-data1').html(data_[0].value);
            $('.table2 .dph-data2').html(data_[1].value + data_[3].value);
            $('.table2 .dph-data3').html(data_[3].value);
            $('.table2 .dph-data4').html(data_[2].value);
            $('.table2 .dph-data5').html(data_[1].value);
            $('.table2 .dph-data6').html(data_[4].value + data_[5].value);
            $('.table2 .dph-data7').html(data_[4].value);
            $('.table2 .dph-data8').html(data_[5].value);
            $('.table2 .dph-data9').html(data_[6].value);
        }

        series_data = [
            { // For shadow
                type: 'bar',
                barWidth: 20,
                xAxisIndex: 2,
                tooltip: {
                    show: false
                },
                itemStyle: {
                    normal: {
                        color: 'rgba(14, 148, 235, 0.102)'
                    }
                },
                data: dataShadow,
                animation: false
            },
            {
                name: '入庫件',
                type: 'bar',
                barGap: '-100%',
                barWidth: '40%',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: '#0e94eb'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [data_[0], 0, 0, 0, 0],
            },
            {
                name: '滯留件',
                type: 'bar',
                stack: '在庫件',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(239,176,19,.9)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, data_[1], 0, 0, 0],
            },
            {
                name: '丟失件',
                type: 'bar',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(239,176,19,0.4)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, 0, 0, 0, data_[2]],
            },
            {
                name: '正常件',
                type: 'bar',
                stack: '在庫件',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(239,176,19,0.3)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, data_[3], 0, 0, 0],
            },
            {
                name: '派送件',
                type: 'bar',
                stack: '出庫件',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(196,64,239,0.8)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, 0, data_[4], 0, 0],
            },
            {
                name: '自提件',
                type: 'bar',
                stack: '出庫件',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(196,64,239,0.4)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, 0, data_[5], 0, 0],
            },
            {
                name: '退簽件',
                type: 'bar',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(219,44,44,0.8)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, 0, 0, data_[6], 0],
            }
        ]


    } else if (type == 2) {
        data = ['入庫件', '在庫件', '出庫件', '丟失件', '撤銷件'];
        if (chartType == '') {
            $('.mail-data1').html(data_[0].value);
            $('.mail-data2').html(data_[2].value + data_[5].value);
            $('.mail-data3').html(data_[1].value);
            $('.mail-data4').html(data_[2].value);
            $('.mail-data5').html(data_[3].value);
            $('.mail-data6').html(data_[4].value);
            $('.mail-data7').html(data_[5].value);
        } else if (chartType == 0) {
            $('.table1 .mail-data1').html(data_[0].value);
            $('.table1 .mail-data2').html(data_[2].value + data_[5].value);
            $('.table1 .mail-data3').html(data_[1].value);
            $('.table1 .mail-data4').html(data_[2].value);
            $('.table1 .mail-data5').html(data_[3].value);
            $('.table1 .mail-data6').html(data_[4].value);
            $('.table1 .mail-data7').html(data_[5].value);
        } else if (chartType == 1) {
            $('.table2 .mail-data1').html(data_[0].value);
            $('.table2 .mail-data2').html(data_[2].value + data_[5].value);
            $('.table2 .mail-data3').html(data_[1].value);
            $('.table2 .mail-data4').html(data_[2].value);
            $('.table2 .mail-data5').html(data_[3].value);
            $('.table2 .mail-data6').html(data_[4].value);
            $('.table2 .mail-data7').html(data_[5].value);
        }

        series_data = [
            { // For shadow
                type: 'bar',
                barWidth: 20,
                xAxisIndex: 2,
                tooltip: {
                    show: false
                },
                itemStyle: {
                    normal: {
                        color: 'rgba(14, 148, 235, 0.102)'
                    }
                },
                data: dataShadow,
                animation: false
            },
            {
                name: '入庫件',
                barGap: '-100%',
                barWidth: '40%',
                type: 'bar',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: '#0e94eb'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [data_[0], 0, 0, 0, 0],
            },
            {
                name: '正常件',
                type: 'bar',
                stack: '在庫件',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(239,176,19,.9)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, data_[5], 0, 0, 0, 0],
                },
            {
                name: '丟失件',
                type: 'bar',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(239,176,19,.9)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, 0, 0, data_[1], 0],
                    },
            {
                name: '滯留件',
                type: 'bar',
                xAxisIndex: 1,
                stack: '在庫件',
                itemStyle: {
                    normal: {
                        color: 'rgba(239,176,19,0.4)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },

                data: [0, data_[2], 0, 0, 0],
                    },
            {
                name: '撤銷件',
                type: 'bar',
                xAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color: 'rgba(239,176,19,0.3)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, 0, 0, 0, data_[3]],
                    },
            {
                name: '出庫件',
                type: 'bar',
                xAxisIndex: 1,
                stack: '退簽件',
                itemStyle: {
                    normal: {
                        color: 'rgba(196,64,239,0.8)'
                    },
                    emphasis: {
                        opacity: 1
                    }
                },
                data: [0, 0, data_[4], 0, 0],
                    }

                    ]
    }

    var option = {
        title: '',
        grid: {
            top: '10%',
            containLabel: true
        },
        tooltip: {
            show: true
        },
        xAxis: [{
                type: 'category',
                show: false,
                data: data,
                axisLabel: {
                    textStyle: {
                        color: '#fff'
                    }
                }
            },
            {
                type: 'category',
                position: "bottom",
                data: data,
                boundaryGap: true,
                // offset: 40,
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#fff'
                    }
                }
            },
            {
                show: false,
                data: dataShadow,
                axisLabel: {
                    inside: true,
                    textStyle: {
                        color: '#fff'
                    }
                },
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                z: 10
        },
        ],
        yAxis: [{
                show: true,
                splitLine: {
                    show: false,
                    lineStyle: {
                        color: "#0e94eb"
                    }
                },
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                axisLabel: {
                    show: true,
                    color: '#0e94eb'
                }
        }, {
                show: false,
                type: "value",
                nameTextStyle: {
                    color: '#0e94eb'
                },
                axisLabel: {
                    color: '#0e94eb'
                },
                splitLine: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                }
        },
            {
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#999'
                    }
                }
                }],
        //        color: ['#e54035'],
        series: series_data
    }
    if (chartType === '') {
        myChart.clear();
        myCharts.clear();
        myChart.setOption(option);
        myCharts.setOption(option);
    } else if (chartType === 0) {
        myChart.clear();
        myChart.setOption(option);
    } else if (chartType === 1) {
        myCharts.clear();
        myCharts.setOption(option);
    }
}

chart3(1, '')
    //
    //
    //
$('#dateBtn').on('click', function () {
    if ($('#timeBox').is(":hidden")) {
        $('#timeBox').show();
        document.getElementById('timeBox').focus();

    } else {
        $('#timeBox').hide();
    }
})

$('#dateBtns').on('click', function () {
    if ($('#timeBoxs').is(":hidden")) {
        $('#timeBoxs').show();
        document.getElementById('timeBoxs').focus();

    } else {
        $('#timeBoxs').hide();
    }
})

$('#switchBtn').on('click', 'span', function () {
    $(this).addClass('active').siblings().removeClass('active');
    if ($(this).data('datatype') == 'income') {
        $('#totalProfit').html('123,456.5元');
    } else if ($(this).data('datatype') == 'expend') {
        $('#totalProfit').html('32,111.4元');
    }
})

$('#tabBtn').on('click', function () {
    var _this = $(this);
    if ($('.right-top').children('.chart-box').is(':hidden')) {
        _this.children('span').html('圖表');
        $('.right-top').children('.chart-box').show().siblings('.data-box').hide();

    } else {
        _this.children('span').html('表格');
        $('.right-top').children('.data-box').show().siblings('.chart-box').hide();
        if (_this.data('state') == 1) {
            $('.table1').eq(0).show().siblings('table').hide();
        } else if (_this.data('state') == 2) {
            $('.table1').eq(1).show().siblings('table').hide();
        }
    }
})


$('#tabBtns').on('click', function () {
    var _this = $(this);
    if (_this.siblings('.pop-chart').is(':hidden')) {
        _this.children('span').html('圖表');
        _this.siblings('.pop-chart').show().siblings('.data-box').hide();

    } else {
        _this.children('span').html('表格');
        _this.siblings('.data-box').show().siblings('.chart-box').hide();
        if (_this.data('state') == 1) {
            $('.table2').eq(0).show().siblings('table').hide();
        } else if (_this.data('state') == 2) {
            $('.table2').eq(1).show().siblings('table').hide();
        }
    }
})




//時間選擇器
var startV = '';
var endV = '';
laydate.skin('danlan');
var startTime = {
    elem: '#startTime',
    format: 'YYYY-MM-DD',
    min: '1997-01-01', //設定最小日期為當前日期
    max: laydate.now(), //最大日期
    istime: true,
    istoday: true,
    fixed: false,
    choose: function (datas) {
        startV = datas;
        endTime.min = datas; //開始日選好后，重置結束日的最小日期
    }
};
var endTime = {
    elem: '#endTime',
    format: 'YYYY-MM-DD',
    min: laydate.now(),
    max: laydate.now(),
    istime: true,
    istoday: true,
    fixed: false,
    choose: function (datas) {
        //        startTime.max = datas; //結束日選好后，重置開始日的最大日期
        endV = datas;
    }
};

laydate(startTime);
laydate(endTime);

//時間選擇器
var startVs = '';
var endVs = '';
laydate.skin('danlan');
var startTimes = {
    elem: '#startTimes',
    format: 'YYYY-MM-DD',
    min: '1997-01-01', //設定最小日期為當前日期
    max: '2099-06-16', //最大日期
    istime: true,
    istoday: true,
    fixed: false,
    choose: function (datas) {
        startVs = datas;
        endTimes.min = datas; //開始日選好后，重置結束日的最小日期
        setQgData($('#barTypes').parent().parent(), 1);
    }
};
var endTimes = {
    elem: '#endTimes',
    format: 'YYYY-MM-DD',
    min: laydate.now(),
    max: laydate.now(),
    istime: true,
    istoday: true,
    fixed: false,
    choose: function (datas) {
        //        startTime.max = datas; //結束日選好后，重置開始日的最大日期
        endVs = datas;
        setQgData($('#barTypes').parent().parent(), 1);
    }
};

laydate(startTimes);
laydate(endTimes);

//點擊時間選擇器的時候更改樣式
$('#endTime').on('click', function () {
    dateCss();
})

$('#end').on('click', function () {
    dateCss();
})


//更改日期插件的樣式
function dateCss() {
    var arr = $('#laydate_box').attr('style').split(';');
    var cssStr =
        'position:absolute;right:0;';
    for (var i = 0; i < arr.length; i++) {
        if (arr[i].indexOf('top') != -1) {
            cssStr += arr[i];
        }
    }

    $('#laydate_box').attr('style', cssStr);
}



//chart4Data模擬數據
var chart4Data = [{
    'name': "天津市",
    'value': 178546
    }, {
    'name': "湖南省",
    'value': 125687
    }, {
    'name': "福建省",
    'value': 78452
    }, {
    'name': "北京市",
    'value': 57841
    }, {
    'name': "江蘇省",
    'value': 45879
    }, {
    'name': "海南",
    'value': 28584
    }, {
    'name': "四川省",
    'value': 14852
    }, {
    'name': "浙江省",
    'value': 12589
    }, {
    'name': "重慶市",
    'value': 5261
    }, {
    'name': "香港特別行政區",
    'value': 2563
    }, {
    'name': "內蒙古",
    'value': 856
    }]
chart4(chart4Data, 1, '');

function chart4(data, type, chartType) {
    var str = '<li><span></span><p>城市</p><p>派件</p></li>';
    for (var i = 0; i < 10; i++) {
        str += '<li><span>' + (i + 1) + '</span><p>' + data[i].name + '</p><p>' + data[i].value + '</p></li>';
    }

    var s_data = [];
    var myChart = echarts.init(document.getElementById('chart4'));
    var myCharts = echarts.init(document.getElementById('chart4s'));
    window.addEventListener('resize', function () {
        myChart.resize();
        myCharts.resize();
    });


    function formtGCData(geoData, data, srcNam, dest) {
        var tGeoDt = [];
        if (dest) {
            for (var i = 0, len = data.length; i < len; i++) {
                if (srcNam != data[i].name) {
                    tGeoDt.push({
                        coords: [geoData[srcNam], geoData[data[i].name]],
                    });
                }
            }
        } else {
            for (var i = 0, len = data.length; i < len; i++) {
                if (srcNam != data[i].name) {
                    tGeoDt.push({
                        coords: [geoData[data[i].name], geoData[srcNam]],
                    });
                }
            }
        }
        return tGeoDt;
    }

    function formtVData(geoData, data, srcNam) {
        var tGeoDt = [];
        for (var i = 0, len = data.length; i < len; i++) {
            var tNam = data[i].name
            if (srcNam != tNam) {
                tGeoDt.push({
                    name: tNam,
                    symbolSize: 2,
                    itemStyle: {
                        normal: {
                            color: '#ffeb40',
                        }
                    },
                    value: geoData[tNam]
                });
            }

        }
        tGeoDt.push({
            name: srcNam,
            value: geoData[srcNam],
            symbolSize: 5,
            itemStyle: {
                normal: {
                    color: '#2ef358',
                }
            }

        });
        return tGeoDt;
    }

    var planePath = 'pin';
    if (type == 2) {
        s_data.push({
            type: 'lines',
            zlevel: 2,
            mapType: 'china',
            symbol: 'none',
            effect: {
                show: true,
                period: 1.5,
                trailLength: 0.1,
                //                color: '#ffeb40',
                color: '#2ef358',
                symbol: planePath,
                symbolSize: 6,
                trailLength: 0.5

            },
            lineStyle: {
                normal: {
                    color: '#2ef358',
                    width: 1,
                    opacity: 0.4,
                    curveness: 0.2
                }
            },
            data: formtGCData(geoCoordMap, data, '珠海', true)
        })

    } else if (type == 1) {
        s_data.push({
            type: 'lines',
            zlevel: 2,
            effect: {
                show: true,
                period: 1.5,
                trailLength: 0.1,
                //                color: '#2ef358',
                color: '#ffeb40',
                symbol: planePath,
                symbolSize: 6,
                trailLength: 0.5
            },
            lineStyle: {
                normal: {
                    color: '#ffeb40',
                    width: 1,
                    opacity: 0.4,
                    curveness: 0.2
                }
            },
            data: formtGCData(geoCoordMap, data, '珠海', false)
        }, {

            type: 'effectScatter',
            coordinateSystem: 'geo',
            zlevel: 2,
            rippleEffect: {
                period: 4,
                scale: 2.5,
                brushType: 'stroke'
            },
            symbol: 'none',
            symbolSize: 4,
            itemStyle: {
                normal: {
                    color: '#fff'
                }
            },

            data: formtVData(geoCoordMap, data, '珠海')
        })
    }

    var option = {
        tooltip: {
            trigger: 'item',
        },
        geo: {
            map: 'china',
            label: {
                show: true,
                position: 'insideLeft',
                color: 'white',
                fontSize: '10',
                emphasis: {
                    show: true
                }
            },
            roam: true,
            silent: true,
            itemStyle: {
                normal: {
                    areaColor: 'transparent',
                    borderColor: '#0e94eb',
                    shadowBlur: 10,
                    shadowColor: '#0e94ea'
                }
            },
            left: 10,
            right: 10
        },
        series: s_data
    };
    if (chartType === '') {
        $('.ranking-box').html(str);
        myChart.setOption(option);
        myCharts.setOption(option);
    } else if (chartType === 0) {
        $('.center-bottom .ranking-box').html(str);
        myChart.setOption(option);
    } else if (chartType === 1) {
        $('.pop-data .ranking-box').html(str);
        myCharts.setOption(option);
    }
}

$('.close-pop').on('click', function () {
    $(this).parent().parent().hide().find('.cont-div').attr('style', 'visibility: hidden');
})

$('#setBtn').on('click', function () {
    $('.container').attr('style', 'visibility: visible').find('.pop-up').eq(4).attr('style', 'visibility: visible').siblings().attr('style', 'visibility: hidden');

})

var workDate;
var time = {
    elem: '#times',
    format: 'YYYY-MM-DD',
    min: laydate.now(),
    max: laydate.now() + 30,
    istime: true,
    istoday: true,
    fixed: false,
    choose: function (datas) {
        //        startTime.max = datas; //結束日選好后，重置開始日的最大日期
        workDate = datas;
    }
};

laydate(time);

$('#addT').on('click', function () {
    $('#mineusT').show();
    if ($(this).siblings('input').length < 6) {
        if ($(this).siblings('input').length == 5) {
            $(this).hide();
        }
        $(this).before('<input type="text" value="">');
    }

})

$('#mineusT').on('click', function () {
    if ($(this).siblings('input').length > 1) {
        if ($(this).siblings('input').length == 6) {
            $('#addT').show();
        } else if ($(this).siblings('input').length == 2) {
            $(this).hide()
        }
        $(this).siblings('input:last').remove();
    }
})

$('#addL').on('click', function () {
    $('#mineusL').show();
    if ($(this).siblings('input').length < 3) {
        if ($(this).siblings('input').length == 2) {
            $(this).hide();
        }
        $(this).before('<input type="text" value="">');
    }

})

$('#mineusL').on('click', function () {
    if ($(this).siblings('input').length > 1) {
        if ($(this).siblings('input').length == 3) {
            $('#addL').show();
        } else if ($(this).siblings('input').length == 2) {
            $(this).hide()
        }
        $(this).siblings('input:last').remove();
    }
})