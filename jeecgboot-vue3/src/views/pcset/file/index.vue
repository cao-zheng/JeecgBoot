<template>
    <h1>{{titlePath(fileVo)}}</h1>
    <table style="width: 100%;table-layout: fixed;">
        <tr>
            <td>文件名</td>
            <td>大小</td>
            <td>最后更新时间</td>
            <td>下次次数</td>
        </tr>
        <tr v-show="isShowDirectBack(fileVo)">
            <td>
                <span style="color: blue;text-decoration: underline;cursor: pointer;"
                @click="getBackPackageFileList(fileVo)"
                >
                    ../
                </span>
            </td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr v-for="item in tableData">
            <td>
                <span style="color: blue;text-decoration: underline;cursor: pointer;"
                @click="getPackageFileList(item)"
                >
                    {{item.name}}
                </span>
            </td>
            <td>
                {{item.size}}
            </td>
            <td>
                {{item.updateTime}}
            </td>
            <td>
                {{item.downloadCount}}
            </td>
        </tr>
    </table>
</template>
<script lang="ts" setup>
    import {ref,onMounted,reactive} from 'vue';
    import { defHttp } from '/@/utils/http/axios';
    import { message } from 'ant-design-vue';

    const rootPath = "/home/ftpFileHome/"
    //const rootPath = "D:\\FtpFileHome\\" //win
    const relatePath = "PCSet_Release"
    const tableData = ref([])
    const fileVo = reactive({
        name: '',
        updateTime: '',
        size:'',
        downloadCount:'',
        remark:'',
        relatePath:'',
        type:''
    })
    onMounted(() => {
        fileVo.relatePath = rootPath + relatePath;
        fileVo.type = "directory";

        getPackageFileList(fileVo);
    })

    let titlePath = (item)=>{
        let resultPath =  item.relatePath.replace(rootPath, '');
        return resultPath;
    }

    const isShowDirectBack = (item)=>{
        if(item.relatePath == (rootPath + relatePath)) return false;
        else return true; 
    }

    const getBackPackageFileList = (item)=>{
        let packagePath  = item.relatePath.split(/[\\/]/).filter(Boolean);
        if (packagePath.length > 1) {
            packagePath.pop(); // 移除最后一个段
            item.relatePath = '/' + packagePath.join('/');
            //item.relatePath = packagePath.join('\\'); // win 将数组转换回字符串路径
        }
        getPackageFileList(item)
    }

    const sortVersions = (versions) =>{
        versions.sort((a, b) => {
            a = a.name.split('.').map(Number);
            b = b.name.split('.').map(Number);
            for (let i = 0; i < 3; i++) {
                if (a[i] === b[i]) {
                    continue; // 相等则比较下一位
                }
                return a[i] - b[i]; // 返回差值，进行排序
            }
            return 0; // 所有位都相等
        });
        return versions;
    }

    const getPackageFileList = (item)=>{
        let params = {"path": item.relatePath};
        if(item.type == 'directory'){
            defHttp.post({
                url: '/jeecg-pcset/pcset/file/package/cur/list',
                params
            },
            { isTransformResponse: false })
            .then(function (response) {
                tableData.value = sortVersions(response);
                fileVo.relatePath = item.relatePath
                fileVo.downloadCount = '-'
            })
            .catch(function (error) {
                console.log(error);
            });
        }else{
            defHttp.post({ 
                url: '/jeecg-pcset/pcset/download',
                params,
                responseType: 'blob'
            },
            { isTransformResponse: false }
            ).then((data)=>{
                if (!data || data.size === 0) {
                    message.warning('文件下载失败');
                    return;
                }
                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    window.navigator.msSaveBlob(new Blob([data]), item.name);
                } else {
                    let url = window.URL.createObjectURL(new Blob([data]));
                    let link = document.createElement('a');
                    link.style.display = 'none';
                    link.href = url;
                    link.setAttribute('download', item.name);
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link); //下载完成移除元素
                    window.URL.revokeObjectURL(url); //释放掉blob对象
                }
            });
        }
    }
</script>