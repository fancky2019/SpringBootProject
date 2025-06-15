$(function () {

    $("#btnSave").click(function () {
        alert("sddssd");
    });

    // 点击按钮发送GET请求
    $("#btnCheckAvailable").click(function() {
        // 显示加载状态
        $("#result").html("加载中...");

        // 发送GET请求
        $.ajax({
            url: "http://10.100.200.32:8088/truckOrderItem/checkAvailable?projectNo=XM0713&deviceNo=2072&materialCode=P0002096844&quantity=1", // 示例API
            type: "GET",
            dataType: "json", // 期望返回JSON格式
            success: function(data) {
                debugger;
                // 请求成功处理
                $("#result").html(`
                            <h3>获取成功js</h3>
                            <p>ID: ${data.traceId}</p>
                            <p> ${JSON.stringify(data, null, 2)}</p>
                        `);
            },
            error: function(xhr, status, error) {
                // 请求失败处理
                $("#result").html(`
                            <h3 style="color:red">请求失败</h3>
                            <p>状态码: ${xhr.status}</p>
                            <p>错误信息: ${error}</p>
                        `);
            },
            complete: function() {
                // 请求完成后的回调（无论成功失败都会执行）
                console.log("请求完成");
            }
        });
    });

});
