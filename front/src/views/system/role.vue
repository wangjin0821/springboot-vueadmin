<template>
  <imp-panel>
    <h3 class="box-title" slot="header" style="width: 100%;">
      <el-button type="primary" icon="plus" @click="newAdd" :loading="addBtnLoading">新增</el-button>
      <el-button type="danger" icon="delete" @click="batchDelete" :loading="batchDelBtnLoading">删除</el-button>
    </h3>
    <el-row slot="body" :gutter="24" style="margin-bottom: 20px;">
      <el-col :span="6" :xs="24" :sm="24" :md="6" :lg="6" style="margin-bottom: 20px;">
        <el-tree v-if="roleTree"
                  :data="roleTree"
                  ref="roleTree"
                  show-checkbox
                  highlight-current
                  default-expand-all
                  :render-content="renderContent"
                  @node-click="handleNodeClick" clearable node-key="id" :props="defaultProps" v-loading="treeLoading"></el-tree>
      </el-col>
      <el-col :span="18" :xs="24" :sm="24" :md="18" :lg="18">
        <el-card class="box-card">
          <div class="text item">
            <el-form :model="form" ref="form">
              <el-form-item label="父级" :label-width="formLabelWidth">
                <!--<el-input v-model="form.parentId" auto-complete="off"></el-input>-->
                <el-select-tree v-model="form.parentId" :treeData="roleTree" :propNames="defaultProps" clearable
                 :handleTreeNodeClickCallback="selectParentAdd" placeholder="请选择父级">
                </el-select-tree>
              </el-form-item>
              <el-form-item label="名称" :label-width="formLabelWidth">
                <el-input v-model="form.roleName" auto-complete="off"></el-input>
              </el-form-item>
              <el-form-item label="角色编码" :label-width="formLabelWidth">
                <el-input v-model="form.roleCode" auto-complete="off"></el-input>
              </el-form-item>
              <el-form-item label="" :label-width="formLabelWidth">
                <el-button type="primary" @click="onSubmit" v-text="form.id?'修改':'新增'" :loading="submitLoading"></el-button>
                <el-button type="info" @click="settingResource(form.id)" icon="setting" v-show="form.id && form.id!=null">配置资源
                </el-button>
                <el-button type="danger" @click="deleteSelected" icon="delete" v-show="form.id && form.id!=null" :loading="delBtnLoading">删除
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
        <el-dialog title="配置资源" :visible.sync="dialogVisible" size="tiny">
          <div class="select-tree">
          <el-scrollbar
            tag="div"
            class='is-empty'
            wrap-class="el-select-dropdown__wrap"
            view-class="el-select-dropdown__list">
          <el-tree
            :data="resourceTree"
            ref="resourceTree"
            show-checkbox
            default-expand-all
            node-key="id"
            v-loading="dialogLoading"
            :props="resourceProps">
          </el-tree>
          </el-scrollbar>
          </div>
          <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="configRoleResources">确 定</el-button>
          </span>
        </el-dialog>
      </el-col>
    </el-row>
  </imp-panel>
</template>
<script>
import panel from '@/components/Panel'
import selectTree from '@/components/SelectTree'
import treeter from '@/utils/treeter'
import { getRoleList, saveRole, deleteRoles, getMenuList, saveRolePermission, getListByRoleId } from '@/api/system'

export default {
  mixins: [treeter],
  components: {
    'imp-panel': panel,
    'el-select-tree': selectTree
  },
  data() {
    return {
      addBtnLoading: false,
      batchDelBtnLoading: false,
      delBtnLoading: false,
      submitLoading: false,
      treeLoading: false,
      dialogLoading: false,
      dialogVisible: false,
      formLabelWidth: '100px',
      defaultProps: {
        children: 'children',
        label: 'roleName',
        id: 'id'
      },
      resourceProps: {
        children: 'children',
        label: 'name',
        id: 'id'
      },
      roleTree: [],
      resourceTree: [],
      maxId: 700000,
      form: {
        id: 0,
        parentId: 0,
        roleName: '',
        roleCode: ''
      }
    }
  },
  methods: {
    configRoleResources() {
      const checkedKeys = this.$refs.resourceTree.getCheckedKeys()
      console.log(checkedKeys)
      const params = []
      checkedKeys.forEach(element => {
        params.push({ roleId: this.form.id, resouceId: element, resouceType: 1 })
      });
      saveRolePermission(params).then(res => {
        this.$message(res.message)
        this.resourceTree = []
        this.dialogVisible = false
      }).catch(error => {
        this.dialogVisible = false
        this.$message.error(error)
      })
      // this.$http.get(api.SYS_SET_ROLE_RESOURCE + "?roleId=" + this.form.id + "&resourceIds="+checkedKeys.join(','))
      //   .then(res => {
      //     this.$message('修改成功')
      //     this.dialogVisible = false
      //   })
    },
    handleNodeClick(data) {
      this.form = data
    },
    newAdd() {
      this.form = {
        id: 0,
        parentId: 0,
        roleName: '',
        roleCode: ''
      }
    },
    selectParentAdd() {
      this.form.id = 0
      this.form.roleName = ''
      this.form.roleCode = ''
    },
    batchDelete() {
      const checkKeys = this.$refs.roleTree.getCheckedKeys()
      if (checkKeys == null || checkKeys.length <= 0) {
        this.$message.warning('请选择要删除的资源')
        return
      }
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.batchDelBtnLoading = true
        deleteRoles({ids: checkKeys}).then((res) => {
          this.batchDelBtnLoading = false
          this.$message(res.message)
          this.load()
        }).catch((error) => {
          this.batchDelBtnLoading = false
          this.$message.error(error)
        })
        // this.$http.get(api.SYS_ROLE_DELETE + "?roleIds=" + checkKeys.join(','))
        //   .then(res => {
        //     this.$message('操作成功')
        //     this.load()
        //   }).catch(e => {
        //   this.$message('操作成功')
        //   console.log(checkKeys)
        //   this.batchDeleteFromTree(this.roleTree, checkKeys)
        // })
      })
    },
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true
          saveRole(this.form).then((res) => {
            this.submitLoading = false
            this.$message(res.message)
            this.newAdd()
            this.load()
          }).catch((error) => {
            this.submitLoading = false
            this.$message.error(error)
          })
        }
      })
      // this.form.parentId = this.form.parentId
      // this.$http.post(api.SYS_ROLE_ADD, this.form)
      //   .then(res => {
      //     this.form.id = res.data.id
      //     this.appendTreeNode(this.roleTree, res.data)
      //   }).catch(e => {
      //   this.maxId += 1
      //   this.$message('操作成功')
      //   this.form.id = this.maxId
      //   var  ddd = {
      //     id: this.form.id,
      //     name: this.form.name,
      //     sort: this.form.sort,
      //     enName:this.form.enName,
      //     parentId: this.form.parentId,
      //     usable: this.form.usable,
      //     children:[]
      //   }
      //   this.appendTreeNode(this.roleTree, ddd)
      //   this.roleTree.push({})
      //   this.roleTree.pop()
      // })
    },
    deleteSelected(id) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.delBtnLoading = true
        deleteRoles({ids: id}).then((res) => {
          this.delBtnLoading = false
          this.$message(res.message)
          this.load()
        }).catch((error) => {
          this.delBtnLoading = false
          this.$message.error(error)
        })
      })
      // this.$http.get(api.SYS_ROLE_DELETE + "?roleIds=" + id)
      //   .then(res => {
      //     this.$message('操作成功')
      //     this.deleteFromTree(this.roleTree, id)
      //     this.newAdd()
      //   }).catch(e =>{
      //   this.$message('操作成功')
      //   this.deleteFromTree(this.roleTree, id)
      //   this.newAdd()
      // })
    },
    load() {
      this.treeLoading = true;
      getRoleList().then(res => {
        this.treeLoading = false
        this.roleTree = []
        this.roleTree.push(...res.data)
      }).catch(error => {
        this.treeLoading = false
        this.$message.error(error)
      })
    },
    renderContent(h, { node, data, store }) {
      return (
        <span>
          <span>
            <span>{node.label}</span>
          </span>
          <span class='render-content'>
            <i class='fa fa-wrench' title='配置资源' on-click={(e) => this.settingResource(data.id)}></i>
            <i class='fa fa-trash' on-click={ () => this.deleteSelected(data.id) }></i>
          </span>
        </span>
      )
    },
    settingResource(id) {
      this.dialogVisible = true
      if (this.resourceTree == null || this.resourceTree.length <= 0) {
        getMenuList().then(res => {
          this.resourceTree.push(...res.data)
          getListByRoleId({roleId: id}).then(res1 => {
            if (res1.data.length > 0) {
              const selectIds = []
              res1.data.forEach(item => {
                selectIds.push(item.resouceId)
              })
              console.log(selectIds)
              this.$refs.resourceTree.setCheckedKeys(selectIds)
            }
          }).catch(error1 => {
            this.dialogLoading = false
            this.$message.error(error1)
          })
        }).catch(error => {
          this.dialogLoading = false
          this.$message.error(error)
        })
        // sysApi.resourceList()
        //   .then(res => {
        //     this.dialogLoading = false
        //     this.resourceTree = res
        //   })
      } else {
        getListByRoleId({roleId: id}).then(res => {
          if (res.data.length > 0) {
            const selectIds = []
            res.data.forEach(item => {
              selectIds.push(item.resouceId)
            })
            console.log(selectIds)
            this.$refs.resourceTree.setCheckedKeys(selectIds)
          }
        }).catch(error => {
          this.dialogLoading = false
          this.$message.error(error)
        })
      }
      // this.$http.get(api.SYS_ROLE_RESOURCE + "?id=" + id)
      //   .then(res => {
      //     this.$refs.resourceTree.setCheckedKeys(res.data)
      //   }).catch(err=> {

      // })
    }
  },
  created() {
    this.load()
  }
}
</script>

<style>
  .render-content {
    float: right;
    margin-right: 20px
  }

  .render-content i.fa {
    margin-left: 10px;
  }

  .render-content i.fa:hover{
    color: #e6000f;
  }

  .select-tree .el-scrollbar.is-empty .el-select-dropdown__list {
    padding: 0;
  }

  .select-tree .el-scrollbar {
    border: 1px solid #d1dbe5;
  }

  .select-tree .el-tree{
    border:0;
  }

</style>
  