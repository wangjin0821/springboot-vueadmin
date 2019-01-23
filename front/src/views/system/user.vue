<template>
  <div>
    <imp-panel>
        <h3 class="box-title" slot="header" style="width: 100%;">
          <el-row style="width: 100%;">
            <el-col :span="12">
              <el-button type="primary" icon="el-icon-plus" @click="add">新增</el-button>
              <!-- <router-link :to="{ path: 'userAdd'}">
                
              </router-link> -->
            </el-col>
            <el-col :span="12">
              <div class="el-input" style="width: 200px; float: right;">
                <i class="el-input__icon el-icon-search"></i>
                <input type="text" placeholder="输入用户名称" v-model="searchKey" @keyup.enter="search($event)"
                        class="el-input__inner">
              </div>
            </el-col>
          </el-row>
        </h3>
        <div slot="body" v-loading="listLoading">
          <el-table
            :data="tableData.rows"
            border
            style="width: 100%"
            @selection-change="handleSelectionChange">
            <!--checkbox 适当加宽，否则IE下面有省略号 https://github.com/ElemeFE/element/issues/1563-->
            <el-table-column
              prop="id"
              type="selection"
              width="50">
            </el-table-column>
            <el-table-column
              label="照片" width="76">
              <template slot-scope="scope">
                <img :src='scope.row.avater' style="height: 35px;vertical-align: middle;" alt="">
              </template>
            </el-table-column>
            <el-table-column
              prop="name"
              label="名称">
            </el-table-column>
            <el-table-column
              prop="userName"
              label="登录用户名">
            </el-table-column>
            <el-table-column
              prop="telphone"
              label="手机号">
            </el-table-column>
            <el-table-column
              label="状态">
              <template slot-scope="scope">
                {{ scope.row.status==1 ? '已激活' : '未激活' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="285">
              <template slot-scope="scope">
                <el-button
                  size="small"
                  type="default"
                  icon="edit"
                  @click="handleEdit(scope.$index, scope.row)">编辑
                </el-button>
                <el-button
                  size="small"
                  type="info"
                  icon="setting"
                  @click="handleRoleConfig(scope.$index, scope.row)">配置角色
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.$index, scope.row)">删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
    
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="tableData.pagination.pageNo"
            :page-sizes="[5, 10, 20]"
            :page-size="tableData.pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="tableData.pagination.total">
          </el-pagination>
        </div>
      </imp-panel>
      <el-dialog title="配置用户角色" :visible.sync="dialogVisible" size="tiny">
        <div class="select-tree">
          <el-scrollbar
            tag="div"
            class='is-empty'
            wrap-class="el-select-dropdown__wrap"
            view-class="el-select-dropdown__list">
            <el-tree
              ref="roleTree"
              :data="roleTree"
              show-checkbox
              default-expand-all
              node-key="id" v-loading="dialogLoading"
              :props="defaultProps">
            </el-tree>
          </el-scrollbar>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="configUserRoles">确 定</el-button>
          </span>
      </el-dialog>
      <el-dialog title="新增用户" :visible.sync="addVisible">
        <div>
          <el-form ref="addForm" :model="addForm" :rules="addFormRules" label-width="120px">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="addForm.name"></el-input>
            </el-form-item>
            <el-form-item label="登录用户名" prop="userName">
              <el-input name="userName" v-model="addForm.userName"></el-input>
            </el-form-item>
            <el-form-item label="登录密码" prop="passWord">
              <el-input type="passWord" v-model="addForm.passWord"></el-input>
            </el-form-item>
            <el-form-item label="手机号码">
                <el-input v-model="addForm.telphone"></el-input>
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="addForm.status"></el-switch>
            </el-form-item>
          </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="addVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveUser" :loading="saveLoading">保 存</el-button>
        </div>
      </el-dialog>
      <el-dialog title="编辑用户" :visible.sync="editVisible">
          <div>
            <el-form ref="editForm" :model="editForm" :rules="addFormRules" label-width="120px">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="editForm.name"></el-input>
              </el-form-item>
              <el-form-item label="登录用户名" prop="userName">
                <el-input name="userName" v-model="editForm.userName"></el-input>
              </el-form-item>
              <el-form-item label="登录密码" prop="passWord">
                <el-input type="passWord" v-model="editForm.passWord"></el-input>
              </el-form-item>
              <el-form-item label="手机号码">
                  <el-input v-model="editForm.telphone"></el-input>
              </el-form-item>
              <el-form-item label="状态">
                <el-switch v-model="editForm.status"></el-switch>
              </el-form-item>
            </el-form>
          </div>
          <div slot="footer" class="dialog-footer">
            <el-button @click="editVisible = false">取 消</el-button>
            <el-button type="primary" @click="editUser" :loading="saveLoading">保 存</el-button>
          </div>
        </el-dialog>
  </div>
</template>

<script>
import panel from '@/components/Panel'
import { getUserList, deleteUser, saveUser, updateUserRole, getUserRoles, getRoleList } from '@/api/system'

export default {
  components: {
    'imp-panel': panel
  },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能小于6位'))
      } else {
        callback()
      }
    }
    return {
      currentRow: {},
      dialogVisible: false,
      addVisible: false,
      editVisible: false,
      dialogLoading: false,
      saveLoading: false,
      defaultProps: {
        children: 'children',
        label: 'roleName',
        id: 'id'
      },
      roleTree: [],
      listLoading: false,
      searchKey: '',
      tableData: {
        pagination: {
          total: 0,
          pageNo: 1,
          pageSize: 10,
          parentId: 0
        },
        rows: []
      },
      addForm: {
        name: '',
        userName: '',
        passWord: '',
        telphone: '',
        status: 0
      },
      addFormRules: {
        name: [{ required: true, trigger: 'blur' }],
        userName: [{ required: true, trigger: 'blur' }],
        passWord: [{ required: true, trigger: 'blur', validator: validatePass }]
      },
      editForm: {
        id: '',
        name: '',
        userName: '',
        passWord: '',
        telphone: '',
        status: 0
      }
    }
  },
  methods: {
    add() {
      this.addForm = {
        name: '',
        userName: '',
        passWord: '',
        telphone: '',
        status: 0
      }
      this.addVisible = true
    },
    saveUser() {
      this.$refs.addForm.validate(valid => {
        if (valid) {
          this.saveLoading = true
          saveUser(this.addForm).then(res => {
            this.saveLoading = false
            this.addVisible = false
            this.$message(res.message)
            this.loadData()
          }).catch(error => {
            this.saveLoading = false
            this.$message.error(error)
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    editUser() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          this.saveLoading = true
          saveUser(this.editForm).then(res => {
            this.saveLoading = false
            this.editVisible = false
            this.$message(res.message)
            this.loadData()
          }).catch(error => {
            this.saveLoading = false
            this.$message.error(error)
          })
        } else {
          return false
        }
      })
    },
    search(target) {
      this.loadData()
    },
    handleSelectionChange(val) {
      console.log(val)
    },
    handleRoleConfig(index, row) {
      this.currentRow = row
      this.dialogVisible = true
      if (this.roleTree.length <= 0) {
        this.dialogLoading = true
        getRoleList().then(res => {
          this.dialogLoading = false
          this.roleTree = res.data
        })
      }
      getUserRoles({ userId: row.id }).then(res => {
        if (res.data.length > 0) {
          const ids = []
          res.data.forEach(data => {
            ids.push(data.roleId)
          })
          this.$refs.roleTree.setCheckedKeys(ids)
        }
      }).catch(error => {
        this.$message.error(error)
      })
    },
    configUserRoles() {
        const checkedKeys = this.$refs.roleTree.getCheckedKeys()
        if (checkedKeys.length < 0) {
          this.$message.error('请选择需要设置的角色')
        }
        const params = []
        checkedKeys.forEach(element => {
          params.push({ userId: this.currentRow.id, roleId: element })
        });
        updateUserRole(params).then(res => {
          this.dialogVisible = false
          this.$message(res.message)
        }).catch(error => {
          this.$message.error(error)
        })
    },
    handleSizeChange(val) {
      this.tableData.pagination.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.tableData.pagination.pageNo = val
      this.loadData()
    },
    handleEdit(index, row) {
      this.editVisible = true
      this.editForm = {...row}
      // console.log(row)
      // this.$router.push({
      //   path: 'userAdd', query: {
      //     id: row.id
      //   }
      // })
    },
    handleDelete(index, row) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteUser({ ids: [row.id] }).then(res => {
          this.$message(res.message)
          this.loadData()
        }).catch(error => {
          this.$message.error(error)
        })
      })
    },
    loadData() {
      this.listLoading = true
      getUserList({
        userName: this.searchKey,
        pagination : {
          size: this.tableData.pagination.pageSize,
          current: this.tableData.pagination.pageNo
        }
      }).then(res => {
        this.listLoading = false
        this.tableData.rows = res.data.list
        this.tableData.pagination.total = res.data.totalCount
        // this.tableData.pagination.pageSize = res.data.totalPage
      }).catch(error => {
        this.listLoading = false
        this.$message.error(error)
      })
    }
  },
  created() {
    this.loadData()
  }
}
</script>
<style>
  .el-pagination {
    float: right;
    margin-top: 15px;
  }
</style>
  