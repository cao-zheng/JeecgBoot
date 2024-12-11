<template>
   <a-row :class="['p-4', `${prefixCls}--box`]" :gutter="10">
    <a-col :xl="6" :lg="8" :md="10" :sm="24" style="flex: 1">
      <a-card :bordered="false" style="height: 100%">
        <a-spin :spinning="loading">
          <BasicTree :treeData="treeData" 
          toolbar 
          search
          :clickRowToExpand="false"
          @select="onSelect"
          @search="onSearch"
          />
        </a-spin>
      </a-card>
    </a-col>
    <a-col :xl="18" :lg="16" :md="14" :sm="24" style="flex: 1">
      <a-card :bordered="false" style="height: 100%">
        col-6 col-pull-18
      </a-card>
    </a-col>
  </a-row>
</template>

<script lang="ts" setup>
  import { provide, ref, onMounted } from 'vue'
  import { defHttp } from '/@/utils/http/axios'
  import { BasicTree } from '/@/components/Tree/index';
  import { useDesign } from '/@/hooks/web/useDesign';
  const { prefixCls } = useDesign('depart-user');
  import { TreeItem } from '/@/components/Tree/index';

  provide('prefixCls', prefixCls);
  let loading = ref<boolean>(false);
  const fileVoList = ref({})

  // 选择行
  function onSelect(selKeys, event) {
      console.log(selKeys[0], event.selectedNodes[0])
  }

  //搜索
  function onSearch(value: string){
    console.log(value)
  }
  const treeData: TreeItem[] = [
    {
      title: 'parent 1',
      key: '0-0',
      icon: 'home|svg',
      children: [
        { title: 'leaf', key: '0-0-0' },
        {
          title: 'leaf',
          key: '0-0-1',
          children: [
            { title: 'leaf', key: '0-0-0-0' },
            { title: 'leaf', key: '0-0-0-1' },
          ],
        },
      ],
    },
    {
      title: 'parent 2',
      key: '1-1',
      icon: 'home|svg',
      children: [
        { title: 'leaf', key: '1-1-0' },
        { title: 'leaf', key: '1-1-1' },
      ],
    },
    {
      title: 'parent 3',
      key: '2-2',
      icon: 'home|svg',
      children: [
        { title: 'leaf', key: '2-2-0' },
        { title: 'leaf', key: '2-2-1' },
      ],
    },
  ];

  onMounted(()=>{
    getList();
  })

  function getList(){
    defHttp.post({ 
        url: '/jeecg-pcset/pcset/file/list',
      },
      { isTransformResponse: false }
    ).then((res)=>{
        fileVoList.value = res.fileVoList;
        res.prevDir;
    });
  }
</script>

<style lang="less">

</style>
