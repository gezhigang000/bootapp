<!DOCTYPE html>
<html data-theme="light">
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>
			Boot Admin
		</title>
		<link rel="stylesheet" href="/admin/adminstatic/bulma.min.css" />
		<link rel="stylesheet" href="/admin/adminstatic/jquery.toast.min.css">
		<link rel="stylesheet" href="/admin/adminstatic/table/dataTables.bulma.css">
		<link rel="stylesheet" href="/admin/adminstatic/table/font-awesome.min.css">
		<link rel="stylesheet" href="/admin/adminstatic/app.css" />
	</head>
	<body>
		<header>
			<div class="container">
				<nav class="navbar" role="navigation" aria-label="main navigation">
					<div class="navbar-menu">
						<div class="navbar-start">
							<a class="navbar-item" href="#">
								Boot Admin
							</a>
						</div>
					</div>
				</nav>
			</div>
		</header>
		<main>
			<div class="container">
				<nav class="navbar" role="navigation" aria-label="main navigation">
					<div class="navbar-end">
						<div class="navbar-item">
							<div class="buttons">
								<a class="button is-primary" id="_btn_reinstall_fromoss">
									<strong>
										从OSS同步并安装
									</strong>
								</a>
								<a class="button is-primary" id="_btn_reinstall">
									<strong>
										重新加载并安装
									</strong>
								</a>
							</div>
						</div>
					</div>
				</nav>
				<table class="table is-striped is-fullwidth" id="_jar_table">
					<thead>
						<tr>
							<th>
								Jar名称
							</th>
							<th>
								最近修改时间
							</th>
							<th style="width:40%;">
								完整路径
							</th>
							<th>
								操作
							</th>
						</tr>
					</thead>
					<tbody>
						<#if applist?? && applist?size gt 0>
							<#list applist as d>
								<tr>
									<td>
										${d.name!}
									</td>
									<td>
										${d.lastUpdated?string('yyyy-MM-dd HH:mm:ss')}
									</td>
									<td>
										${d.url!}
									</td>
									<td>
										<div class="file is-primary">
											<label class="file-label">
												<input _old=${d.name!} class="file-input _input_jar_file" type="file"
												name="file" />
												<span class="file-cta">
													<span class="file-label">
														替换
													</span>
												</span>
											</label>
										</div>
									</td>
								</tr>
							</#list>
						</#if>
					</tbody>
				</table>
			</div>
			<div class="container">
				<div class="tabs" style="margin-top:10px;margin-bottom:0px">
					<ul>
						<li class="is-active">
							应用程序日志
						</li>
					</ul>
					<div class="buttons are-small">
						<button id="_btn_clearLog" class="button is-danger is-small">
							清除所有日志
						</button>
						<button id="_btn_toggleAutoScroll" class="button">
							关闭自动滚动
						</button>
						<button id="_btn_scrollToBottom" class="button">
							滚动至底部
						</button>
					</div>
				</div>
				<div class="log-container" id="loggingText" contenteditable="true">
				</div>
			</div>
		</main>
		<script src="/admin/adminstatic/jquery-3.7.1.min.js">
		</script>
		<script src="/admin/adminstatic/jquery.toast.min.js">
		</script>
		<script src="/admin/adminstatic/socket.js">
		</script>
		<script src="/admin/adminstatic/table/jquery.dataTables.min.js">
		</script>
		<script src="/admin/adminstatic/app.js">
		</script>
		<script>
			let _table = $('#_jar_table').DataTable({
    paging: false,
    info: false,
    searching: false
});


$("._input_jar_file").change(function() {
    var _fileItem = $(this)[0].files[0];
    var _oldJar = $(this).attr('_old')
    var formData = new FormData();
    formData.append('file', _fileItem)
    formData.append('oldJar', _oldJar)

    var rowIndex = _table.cell(this).index();
    console.log('row index:' + rowIndex)
    $.ajax({
        type: "post",
        url: "/admin/jar/upload",
        data: formData,
        async: true,
        cache: false,
        contentType: false,
        processData: false,
        dataType: 'json',
        success: function(data) {
            if (data.code == '000') {
                var d = data.data
                if (d) {
                    _table.cell(rowIndex, 0).data(d.name)
                    _table.cell(rowIndex, 1).data(d.lastUpdated)
                    _table.cell(rowIndex, 2).data(d.url)
                }
                $.toast({
                    heading: '提示',
                    text: 'jar更新成功,新的jar：' + d.name + ',最近修改时间:' + d.lastUpdated,
                    position: 'top-center',
                    stack: false,
                    icon: 'success'
                })
            } else {
                $.toast({
                    heading: '错误',
                    text: data.msg,
                    position: 'top-center',
                    stack: false,
                    icon: 'error'
                })
            }
        }
    });
})
		</script>
	</body>