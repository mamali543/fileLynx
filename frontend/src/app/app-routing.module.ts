import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { BoardUserComponent } from './components/board-user/board-user.component';
import { BoardModeratorComponent } from './components/board-moderator/board-moderator.component';
import { BoardAdminComponent } from './components/board-admin/board-admin.component';
import { DashboardComponent } from './layouts/dashboard/dashboard.component';
import { UsersComponent } from './layouts/users/users.component';
import { GroupesComponent } from './layouts/groupes/groupes.component';
import { AddGroupeComponent } from './components/add-groupe/add-groupe.component';
import { AddMembreComponent } from './components/add-membre/add-membre.component';
import { LogsComponent } from './layouts/logs/logs.component';
import { FilesComponent } from './layouts/files/files.component';
import { AddFileComponent } from './components/add-file/add-file.component';
import { AuthGuard } from './_services/authguard.service';
import { UserDashboardComponent } from './layouts/user-dashboard/user-dashboard.component';
import { MetadataComponent } from './layouts/metadata/metadata.component';
import { AddLabelComponent } from './components/add-label/add-label.component';
import { AddCategorieComponent } from './components/add-categorie/add-categorie.component';
import { MembersettingComponent } from './components/membersetting/membersetting.component';
import { FoldersettingComponent } from './components/foldersetting/foldersetting.component';
import { UserFilesComponent } from './layouts/user-files/user-files.component';
import { FilesettingsComponent } from './components/filesettings/filesettings.component';
import { AddFolderCollabComponent } from './components/add-folder-collab/add-folder-collab.component';
import { FiledetailsComponent } from './components/filedetails/filedetails.component';
import { GroupDetailsComponent } from './components/group-details/group-details.component';
import { AdminDashboardComponent } from './layouts/admin-dashboard/admin-dashboard.component';
import { EntreprisesComponent } from './layouts/entreprises/entreprises.component';
import { UserSearchPromptComponent } from './components/user-search-prompt/user-search-prompt.component';
import { UserSearchResultsComponent } from './layouts/user-search-results/user-search-results.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'user', component: BoardUserComponent },
  { path: 'mod', component: BoardModeratorComponent },
  { path: 'admin', component: BoardAdminComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
  },
  { path: 'admindashboard', component: AdminDashboardComponent },
  { path: 'entreprises', component: EntreprisesComponent },
  { path: 'search', component: UserSearchResultsComponent},
  {
    path: 'userdashboard',
    component: UserFilesComponent,
    children: [
      { path: 'add-folder/:parentId', component: AddFileComponent },
      { path: 'filter', component: UserSearchPromptComponent},
      {
        path: 'folderdetails',
        component: FoldersettingComponent,
        children: [
          { path: 'add-collab/:folderId', component: AddFolderCollabComponent },
        ],
      },
      { path: 'upload/:parentId', component: FilesettingsComponent },
      { path: 'filedetails/:fileId', component: FiledetailsComponent },

    ],
  },
  { path: 'log', component: LogsComponent, canActivate: [AuthGuard] },

  {
    path: 'groups',
    component: GroupesComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'add-groupe', component: AddGroupeComponent },
      { path: 'details/:groupId', component: GroupDetailsComponent },
    ],
  },
  {
    path: 'users',
    component: UsersComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'add-collaborateur', component: AddMembreComponent },
      { path: 'details/:profId', component: MembersettingComponent },
    ],
  },
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  {
    path: 'files',
    component: FilesComponent,
    children: [
      { path: 'add-folder/:parentId', component: AddFileComponent },
      {
        path: 'folderdetails',
        component: FoldersettingComponent,
        children: [
          { path: 'add-collab/:folderId', component: AddFolderCollabComponent },
        ],
      },
      { path: 'upload/:parentId', component: FilesettingsComponent },
      { path: 'filedetails/:fileId', component: FiledetailsComponent },
    ],
  },
  {
    path: 'metadata',
    component: MetadataComponent,
    children: [
      { path: 'add-label', component: AddLabelComponent },
      { path: 'add-categorie', component: AddCategorieComponent },
      { path: 'user-files', component: UserFilesComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
