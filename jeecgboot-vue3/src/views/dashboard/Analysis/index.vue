<template>
  <!-- <IndexChart v-if="indexStyle === 0"></IndexChart>
  <IndexDef v-if="indexStyle === 1"></IndexDef>
  <IndexBdc v-if="indexStyle == 2"></IndexBdc>
  <IndexTask v-if="indexStyle == 3"></IndexTask>
  <div style="width: 100%; text-align: right; margin-top: 20px">
    首页主题：
    <a-radio-group v-model:value="indexStyle">
      <a-radio :value="0">默认</a-radio>
      <a-radio :value="1">销量统计</a-radio>
      <a-radio :value="2">业务统计</a-radio>
      <a-radio :value="3">我的任务</a-radio>
    </a-radio-group>
  </div> -->
  <div v-if="userName == 'pcset'">
    <a-list size="large" bordered :data-source="pcsetData">
    <template #renderItem="{ item }">
      <a-list-item>{{ item }}</a-list-item>
    </template>
  </a-list>
  </div>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import { useUserStore } from '/@/store/modules/user';
  const userStore = useUserStore();
  const userName = (userStore.getUserInfo.username);
  const pcsetData: string[] = [
    '建议使用ftp客户端上传附件，https://www.filezilla.cn/download/client',
    '用户管理，可创建多个ftp管理用户权限配置',
    '附件目录1，维护附件描述信息',
    '附件目录2，内部使用附件目录树',
    '附件目录3，开放附件目录供所有人浏览下载'
  ]
</script>
