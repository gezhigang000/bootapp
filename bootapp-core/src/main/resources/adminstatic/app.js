
$("#_btn_reinstall").click(function() {
      $.get("/admin/reInstall", function(data) {
      if (data.code == '000'){
          $.toast({
              heading: '提示',
              text: data.data,
              position: 'top-center',
              stack: false,
              icon: 'success'
          })
        }else{
            $.toast({
                 heading: '错误',
                 text: data.msg,
                 position: 'top-center',
                 stack: false,
                 icon: 'error'
             })
        }
      });
});

$("#_btn_reinstall_fromoss").click(function() {
      $.get("/admin/reInstallFromOss", function(data, status) {
       if (data.code == '000'){
          $.toast({
              heading: '提示',
              text: data.data,
              position: 'top-center',
              stack: false,
              icon: 'success'
          })
          }else{
             $.toast({
                 heading: '错误',
                 text: data.msg,
                 position: 'top-center',
                 stack: false,
                 icon: 'error'
             })
          }
      });
});