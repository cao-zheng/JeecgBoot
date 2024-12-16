<template>
   <a-row :class="['p-4', `${prefixCls}--box`]" :gutter="10">
    <a-col :xl="6" :lg="8" :md="10" :sm="24" style="flex: 1">
      <a-card :bordered="false" style="height: 100%">
        <a-spin :spinning="loading">
          <BasicTree :treeData="treeData" 
          toolbar 
          :selectedKeys="selectedKeys"
          :clickRowToExpand="false"
          @select="onSelect"
          @search="onSearch"
          />
        </a-spin>
      </a-card>
    </a-col>
    <a-col :xl="18" :lg="16" :md="14" :sm="24" style="flex: 1">
      <a-card :bordered="false" style="height: 100%">
        <!--定义表格-->
        <BasicTable @register="registerTable">
          <!--操作栏-->
          <template #action="{ record }">
            <TableAction :actions="getTableAction(record)" />
          </template>
        </BasicTable>
      </a-card>
    </a-col>
  </a-row>
</template>

<script lang="ts" setup>
      import { message } from 'ant-design-vue';
      import { provide, ref, onMounted } from 'vue'
      import { defHttp } from '/@/utils/http/axios'
      import { BasicTree } from '/@/components/Tree/index';
      import { useDesign } from '/@/hooks/web/useDesign';
      import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
      import { useListPage } from '/@/hooks/system/useListPage';
      const { prefixCls } = useDesign('depart-user');

      provide('prefixCls', prefixCls);
      let loading = ref<boolean>(false);
      const treeData = ref([]);
      const fileData = ref([]);
      const selectedKeys = ref<string[]>([]);
      


      onMounted(()=>{

        getPackageList();
        getPackageAllFiles({"isAll":true});
      })

      function getPackageList(){
        defHttp.post({ 
            url: '/jeecg-pcset/pcset/file/package/list',
          },
          { isTransformResponse: false }
        ).then((res)=>{
          treeData.value = res
          window['treeData'] = treeData;
          selectedKeys.value = [treeData.value[0].key]
        });
      }

      // 选择行
      function onSelect(selKeys, event) {
          console.log(selKeys[0], event.selectedNodes[0])
          if(selKeys[0] != null){
            getPackageAllFiles({"isAll":false,"path":selKeys[0]});
          }else{
            fileData.value = [];
          }
      }

      //搜索
      // function onSearch(value: string){
        
      // }


      function getPackageAllFiles(params){
        defHttp.post({ 
            url: '/jeecg-pcset/pcset/file/list',
            params
          },
          { isTransformResponse: false }
        ).then((res)=>{
          fileData.value = res
        });
      }


      const { tableContext } = useListPage({
        designScope: 'basic-table-demo',
        tableProps: {
          title: '附件列表',
          dataSource: fileData,
          columns: [
            {
              title: 'name',
              dataIndex: 'name',
              key: 'name',
            },
            {
              title: 'updateTime',
              dataIndex: 'updateTime',
              key: 'updateTime',
            },
            {
              title: 'size',
              dataIndex: 'size',
              key: 'size',
            },
            {
              title: 'remark',
              dataIndex: 'remark',
              key: 'remark',
            },
          ],
          size: 'small',
          actionColumn: {
            width: 120,
          },
        },
      });
      // BasicTable绑定注册
      const [registerTable] = tableContext;
      /**
       * 操作栏
       */
      function getTableAction(record): ActionItem[] {
        return [
          {
            label: '下载',
            onClick: handleEdit.bind(null, record),
          },
        ];
      }

      function handleEdit(record) {
        let params = {"path":record.relatePath}
        console.log(record)
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
              window.navigator.msSaveBlob(new Blob([data]), record.name);
            } else {
              let url = window.URL.createObjectURL(new Blob([data]));
              let link = document.createElement('a');
              link.style.display = 'none';
              link.href = url;
              link.setAttribute('download', record.name);
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link); //下载完成移除元素
              window.URL.revokeObjectURL(url); //释放掉blob对象
            }
        });
      }
</script>

<style lang="less">

</style>
